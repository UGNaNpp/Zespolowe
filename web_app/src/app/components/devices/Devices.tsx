"use client";

import { useEffect, useState } from "react";
import axios from "@/app/api/axios/axios";
import { Device } from "../../../types/device";
import styles from "./devicesStyle.module.scss";
import Link from "next/link";

type Props = {
  dict: {
    title: string;
  };
};

export default function Login({ dict }: Props) {
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");

  useEffect(() => {
    const fetchDevices = async () => {
      try {
        const res = await axios.get("/devices/", {
          headers: {
            "Content-Type": "application/json",
          },
        });

        setDevices(Object.values(res.data) as Device[]);
      } catch (err: unknown) {
        if (err instanceof Error) {
          setError(err.message || "Nieznany błąd");
        } else {
          setError("Nieznany błąd");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchDevices();
  }, []);

  const toggleExpand = (id: number) => {
    setExpandedId((prev) => (prev === id ? null : id));
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  };

  const getFilteredAndSortedDevices = () => {
    return devices
      .filter((device) =>
        device.name.toLowerCase().includes(searchTerm.toLowerCase())
      )
      .sort((a, b) =>
        sortOrder === "asc"
          ? a.name.localeCompare(b.name)
          : b.name.localeCompare(a.name)
      );
  };

  return (
    <main className={styles.main}>
      {loading && <p>Ładowanie urządzeń...</p>}
      {error && <p style={{ color: "red" }}>Błąd: {error}</p>}

      {!loading && !error && (
        <div className={styles.menu}>
          <h2>{dict.title}</h2>

          <div className={styles.controls}>
            <input
              type="text"
              placeholder="Szukaj urządzenia..."
              value={searchTerm}
              onChange={handleSearchChange}
            />
            <button onClick={toggleSortOrder}>
              Sortuj {sortOrder === "asc" ? "▲" : "▼"}
            </button>
          </div>

          <ul>
            {getFilteredAndSortedDevices().map((device) => (
              <li key={device.id}>
                <button onClick={() => toggleExpand(device.id)}>
                  <div>{device.name}</div>
                  <div>{expandedId === device.id ? "▲" : "▼"}</div>
                </button>
                <div
                  className={`${styles.deviceDetails} ${
                    expandedId === device.id ? styles.expanded : ""
                  }`}
                >
                  <p>
                    <strong>IP:</strong> {device.AssociatedIP}
                  </p>
                  <p>
                    <strong>MAC:</strong> {device.AssociatedMAC}
                  </p>
                  <p>
                    <strong>Resolution:</strong>{" "}
                    {device.widthResolution}x{device.heightResolution}
                  </p>
                  <p>
                    <strong>Recording Mode:</strong>{" "}
                    {device.recordingMode ? "On" : "Off"}
                  </p>
                  <p>
                    <strong>Recording Video:</strong>{" "}
                    {device.recordingVideo ? "Yes" : "No"}
                  </p>
                  <p className={styles.buttonParagrapgh}>
                    <Link href={`/stream/${device.id}`}>
                      <button className={styles.streamButton}>Zobacz podgląd</button>
                    </Link>
                  </p>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}
    </main>
  );
}
