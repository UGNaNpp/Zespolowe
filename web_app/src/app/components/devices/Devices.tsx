"use client";

import { useEffect, useState } from "react";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { Device } from "../../../types/device";
import styles from "./devicesStyle.module.scss";
import Link from "next/link";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";

type Props = {
  dict: {
    title: string;
  };
  ApiErrorsDict: {
    "unknown": string;
    "400": string;
    "401": string;
    "403": string;
    "404": string;
    "500": string;
  }
};

export default function Devices({ dict, ApiErrorsDict }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");

  useEffect(() => {
    apiRequest<Device[]>({
      endpoint: "/devices/",
      method: "GET",
      onSuccess: (data) => {
        setDevices(Object.values(data));
      },
      onError: (errMsg) => {
        setError(errMsg);
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    }).finally(() => {
      setLoading(false);
    });
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
    <>
    <main className={styles.main}>
      {!loading && !error && (
        <div className={styles.menu}>
          <h2>{dict.title}</h2>
          <div className={styles.controls}>
            <div className={styles.controls_left}>
              <input
                type="text"
                placeholder="Szukaj urządzenia..."
                value={searchTerm}
                onChange={handleSearchChange}
              />
              <button onClick={toggleSortOrder}>
                <span>
                  Sortuj
                </span>
                <i className={
                    sortOrder === "asc"
                      ? "fa-solid fa-arrow-up-long"
                      : "fa-solid fa-arrow-down-long"
                  }
                />
              </button>
            </div>
            <div className={styles.controls_right}>
              <button onClick={() => {
                  setToastMessage("Funkcja dodawania jeszcze niegotowa");
                  setShowToast(true);
                }}>
                Dodaj nowe urządzenie
              </button>
            </div>
          </div>

          <ul>
            {getFilteredAndSortedDevices().map((device) => (
              <li key={device.id}>
                <button onClick={() => toggleExpand(device.id)}>
                  <div className={styles.deviceName}>
                    <strong>{device.name}</strong>
                  </div>
                  <i className={
                      expandedId === device.id
                        ? "fa-solid fa-chevron-up"
                        : "fa-solid fa-chevron-down"
                    }
                  />
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
    <ErrorToast
      message={toastMessage}
      show={showToast}
      onHide={() => setShowToast(false)}
    />
    </>
  );
}
