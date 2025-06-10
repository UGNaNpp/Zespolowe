"use client";

import { useEffect, useState } from "react";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { Device } from "@/types/device";
import styles from "@/app/components/devices/devicesStyle.module.scss";
import Link from "next/link";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";
import { DeviceEditForm } from "@/app/components/deviceForm/deviceForm";

type Props = {
  dict: {
    pageTitle: string;
    title: string;
    search: string;
    sort: string;
    addNew: string;
    ip: string;
    mac: string;
    resolution: string;
    recordingMode: string;
    recordingVideo: string;
    details: string;
    off: string;
    on: string;
    no: string;
    yes: string;
  },
  deviceFormDict: {
    "addNew": string;
    "modify": string;
    "name": string;
    "ip": string;
    "mac": string;
    "resolution": string;
    "recordingMode": string;
    "recordingVideo": string;
    "save": string;
    "add": string;
    "required": string;
    "maxName": string;
    "invalidIP": string;
    "invalidMAC": string;
    "widthPositive": string;
    "widthMax": string;
    "heightPositive": string;
    "heightMax": string;
    "updated": string;
    "added": string;
  },
  ApiErrorsDict: {
    "unknown": string;
    "400": string;
    "401": string;
    "403": string;
    "404": string;
    "500": string;
  }
};

export default function Devices({ dict, deviceFormDict, ApiErrorsDict }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [showAddForm, setShowAddForm] = useState(false);
  const [refreshFlag, setRefreshFlag] = useState(false);

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

  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refreshFlag]);

  const toggleExpand = (id: number) => {
    setExpandedId((prev) => (prev === id ? null : id));
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const toggleSortOrder = () => {
    setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"));
  };

  const handleAddClick = () => {
    setShowAddForm(true);
  };

  const cancelAddClick = () => {
    setShowAddForm(false);
  }

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
                placeholder={dict.search}
                value={searchTerm}
                onChange={handleSearchChange}
              />
              <button onClick={toggleSortOrder}>
                <span>
                  {dict.sort}
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
                  handleAddClick()
                }}>
                {dict.addNew}
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
                    <strong>{dict.ip}:</strong> {device.AssociatedIP}
                  </p>
                  <p>
                    <strong>{dict.mac}:</strong> {device.AssociatedMAC}
                  </p>
                  <p>
                    <strong>{dict.resolution}:</strong>{" "}
                    {device.widthResolution}x{device.heightResolution} px
                  </p>
                  <p>
                    <strong>{dict.recordingMode}:</strong>{" "}
                    {device.recordingMode ? dict.on : dict.off}
                  </p>
                  <p>
                    <strong>{dict.recordingVideo}:</strong>{" "}
                    {device.recordingVideo ? dict.yes : dict.no}
                  </p>
                  <p className={styles.buttonParagrapgh}>
                    <Link href={`/device/${device.id}`}>
                      <button className={styles.streamButton}>{dict.details}</button>
                    </Link>
                  </p>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}
    </main>
    {showAddForm && (
      <div className={styles.modalOverlay}>
        <div className={styles.formModal}>
          <DeviceEditForm
            modify={false}
            dict={deviceFormDict}
            ApiErrorsDict={ApiErrorsDict}
            onClose={cancelAddClick}
            successPostAction={() => {
              setRefreshFlag(prev => !prev);
              cancelAddClick();
            }}
          />
        </div>
      </div>
    )}
    <ErrorToast
      message={toastMessage}
      show={showToast}
      onHide={() => setShowToast(false)}
    />
    </>
  );
}
