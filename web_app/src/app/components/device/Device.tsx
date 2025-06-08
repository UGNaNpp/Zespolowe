"use client";

import { useEffect, useState } from "react";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { Device } from "@/types/device";
import styles from "@/app/components/device/deviceStyle.module.scss";
import Link from "next/link";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";

type Props = {
  deviceId: string;
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

export default function Devices({ deviceId, dict, ApiErrorsDict }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [device, setDevice] = useState<Device | undefined>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [devicePingStatus, setDevicePingStatus] = useState<boolean | null>(null);

  // testowy
  useEffect(() => {
  const testDevice: Device = {
    id: 1,
    name: 'Test Device',
    AssociatedIP: '192.168.0.100',
    AssociatedMAC: '00:1A:2B:3C:4D:5E',
    heightResolution: 1080,
    widthResolution: 1920,
    recordingMode: true,
    recordingVideo: false,
  };

  setDevice(testDevice);
  setLoading(false);
}, []);

  // useEffect(() => {
  //   apiRequest<Device[]>({
  //     endpoint: `/devices/${deviceId}`,
  //     method: "GET",
  //     onSuccess: (data) => {
  //       setDevices(Object.values(data));
  //     },
  //     onError: (errMsg) => {
  //       setError(errMsg);
  //       setToastMessage(errMsg);
  //       setShowToast(true);
  //     },
  //     dict: ApiErrorsDict,
  //   }).finally(() => {
  //     setLoading(false);
  //   });

  // // eslint-disable-next-line react-hooks/exhaustive-deps
  // }, []);

  return (
    <>
    <main className={styles.main}>
      {!loading && !error && (
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
            <button>{dict.edit}</button>
            <button className={styles.deleteButton}>{dict.delete}</button>
          </div>
          <Link href={`/stream/${device.id}`}>
            <button className={styles.streamButton}>{dict.stream}</button>
          </Link>
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
