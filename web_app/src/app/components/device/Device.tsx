"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { Device } from "@/types/device";
import styles from "@/app/components/device/deviceStyle.module.scss";
import Link from "next/link";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";
import { DeviceEditForm } from "@/app/components/deviceForm/deviceForm";


type Props = {
  deviceId: string;
  dict: {
    pageTitle: string;
    off: string;
    on: string;
    no: string;
    yes: string;
    connection: string;
    resolution: string;
    recordingSettings: string;
    recordingMode: string;
    recordingVideo: string;
    ip: string;
    mac: string;
    id: string;
    px: string;
    isup: string;
    connecting: string;
    online: string;
    offline: string;
    edit: string;
    delete: string;
    stream: string;
    deleteConfirm: string;
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

export default function Devices({ deviceId, dict, deviceFormDict, ApiErrorsDict }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [device, setDevice] = useState<Device | undefined>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [devicePingStatus, setDevicePingStatus] = useState<boolean | null>(null);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const [showEditForm, setShowEditForm] = useState(false);

  const router = useRouter();

  const handleDeleteClick = () => {
    setShowConfirmDelete(true);
  };

  const cancelDelete = () => {
    setShowConfirmDelete(false);
  };

  const confirmDelete = () => {
    setShowConfirmDelete(false);
    setLoading(true);

    apiRequest<void>({
      endpoint: `/devices/id/${deviceId}`,
      method: "DELETE",
      onSuccess: () => {
        setShowToast(true);
        router.push("/devices");
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    }).finally(() => {
      setLoading(false);
    });
  };

  const handleEditClick = () => {
    setShowEditForm(true);
  };

  const cancelEditClick = () => {
    setShowEditForm(false);
  }

  useEffect(() => {
    apiRequest<Device>({
      endpoint: `/devices/id/${deviceId}`,
      method: "GET",
      onSuccess: (data) => {
        setDevice(data);
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
  }, []);

  return (
    <>
    <main className={styles.main}>
      {!loading && !error && device && (
        <div className={styles.menu}>
          <div>
            <h2>{device.name}</h2>
            <div>{dict.id}: {device.id}</div>
            <div className={styles.info}>
              <div className={styles.deviceStatus}>
                <strong className={styles.statusLabel}>{dict.isup}:</strong>{' '}
                {devicePingStatus === null ? (
                  <span className={styles.statusLoading}>{dict.connecting}...</span>
                ) : devicePingStatus ? (
                  <span className={styles.statusOnline}>{dict.online}</span>
                ) : (
                  <span className={styles.statusOffline}>{dict.offline}</span>
                )}
              </div>
              <div>
                <div><strong>{dict.connection}</strong></div>
                <ul>
                  <li>{dict.ip}: {device.AssociatedIP}</li>
                  <li>{dict.mac}: {device.AssociatedMAC}</li>
                </ul>
              </div>
              <div>
                <div><strong>{dict.resolution}</strong></div>
                {device.widthResolution} x {device.heightResolution} {dict.px}
              </div>
              <div>
                <div><strong>{dict.recordingSettings}</strong></div>
                <ul>
                  <li>{dict.recordingMode}: {device.recordingMode ? dict.on : dict.off}</li>
                  <li>{dict.recordingVideo}: {device.recordingVideo ? dict.yes : dict.no}</li>
                </ul>
              </div>
            </div>
          </div>
          <div className={styles.controls}>
            <button onClick={handleEditClick}>{dict.edit}</button>
            <button className={styles.deleteButton} onClick={handleDeleteClick}>{dict.delete}</button>
          </div>
          <Link href={`/stream/${device.id}`}>
            <button className={styles.streamButton}>{dict.stream}</button>
          </Link>
        </div>
      )}
    </main>
    {showConfirmDelete && (
      <div className={styles.modalOverlay}>
        <div className={styles.confirmModal}>
          <p>{dict.deleteConfirm}</p>
          <div className={styles.modalButtons}>
            <button onClick={confirmDelete}>{dict.yes}</button>
            <button onClick={cancelDelete}>{dict.no}</button>
          </div>
        </div>
      </div>
    )}
    {showEditForm && device && (
      <div className={styles.modalOverlay}>
        <div className={styles.formModal}>
          <DeviceEditForm
            device={device}
            deviceId={deviceId}
            modify={true}
            dict={deviceFormDict}
            ApiErrorsDict={ApiErrorsDict}
            onClose={cancelEditClick}
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
