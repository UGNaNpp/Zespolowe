'use client';

import styles from '@/app/components/dashboard/dashboardStyle.module.scss';
import { useSession } from "next-auth/react";
import DiscChart from '@/app/components/dashboard/DiscChart';
import Image from 'next/image';
import Link from "next/link";
import { useEffect, useState } from "react";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";
import { apiRequest } from "@/app/api/axios/apiRequest";
import type { Device } from "@/types/device";

type Props = {
  dict: {
    pageTitle: string;
    used: string;
    free: string;
    greeting: string;
    quickAccess: string;
    discSpaceUsed: string;
    devicesQuickAccess: string;
    devicesOnline: string;
    noDevicesAvailable: string;
    loadingDevices: string;
    devices: string;
    media: string;
    settings: string;
  };
  ApiErrorsDict: {
    unknown: string;
    "400": string;
    "401": string;
    "403": string;
    "404": string;
    "500": string;
  };
};

export default function Dashboard({ dict, ApiErrorsDict }: Props) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { data: session, status } = useSession();
  const [showToast, setShowToast] = useState(false);
  const [devices, setDevices] = useState<Device[]>([]);
  const [upDevices, setUpDevices] = useState<Device[]>([]);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [pingLoaded, setPingLoaded] = useState(false);
  const [onlineDevices, setOnlineDevices] = useState<Device[]>([]);
  const [discSpace, setDiscSpace] = useState<number | null>(null);
  const [recordsSize, setRecordsSize] = useState<number | null>(null);

  const name = session?.user?.name;
  const email = session?.user?.email;
  const avatarUrl = session?.user?.image;
  const firstName = name?.split(' ')[0] || email?.split('@')[0];

  const data =
    discSpace !== null && recordsSize !== null
      ? [
          {
            name: dict.used,
            value: recordsSize,
            fill: '#768474',
          },
          {
            name: dict.free,
            value: Math.max(discSpace - recordsSize, 0),
            fill: '#f4f3ee',
          },
        ]
      : [];

    useEffect(() => {
    apiRequest<Device[]>({
      endpoint: "/devices/",
      method: "GET",
      onSuccess: (data) => {
        setDevices(Object.values(data));
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    })

    apiRequest<Device[]>({
      endpoint: `/active-cameras`,
      method: "GET",
      onSuccess: (data) => {
        setUpDevices(data)
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    }).finally(() => {
      setPingLoaded(true);
    });

    apiRequest<number>({
      endpoint: `/avaible-disk-space`,
      method: "GET",
      onSuccess: (data) => {
        const gb = Number(data) / (1024 ** 3);
        const rounded = Number(gb.toFixed(2));
        setDiscSpace(rounded)
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    })

    apiRequest<number>({
      endpoint: `/records/size`,
      method: "GET",
      onSuccess: (data) => {
        const gb = Number(data) / (1024 ** 3);
        const rounded = Number(gb.toFixed(2));
        setRecordsSize(rounded)
      },
      onError: (errMsg) => {
        setToastMessage(errMsg);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    })

  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (devices.length > 0 && Object.keys(upDevices).length > 0) {
      const filtered = devices.filter(device => upDevices[device.id]);
      setOnlineDevices(filtered);
    }
  }, [devices, upDevices]);

  return(
    <>
      <main className={styles.main}>
        <div className={styles.menu}>
          <div className={styles.menuTop}>
            <div className={styles.menuTopLeft}>
              {session && firstName && (
                <div className={styles.userData}>
                  <Image
                    className={styles.avatar}
                    src={avatarUrl ?? '/default-avatar.png'}
                    alt="User logo"
                    width={30}
                    height={30}
                    priority
                  />
                  <h2>{dict.greeting}, {firstName}!</h2>
                </div>
              )}
              <div>{dict.quickAccess}:</div>
              <div className={styles.quickAccess}>
                <ul className={styles.menuList}>
                  <li>
                    <Link href="/devices">
                      <i className="fa-regular fa-compass"></i>
                      <span>{dict.devices}</span>
                    </Link>
                  </li>
                  <li>
                    <Link href="/media">
                      <i className="fa-regular fa-folder-open"></i>
                      <span>{dict.media}</span>
                    </Link>
                  </li>
                  <li>
                    <Link href="/settings">
                      <i className="fa-solid fa-gear"></i>
                      <span>{dict.settings}</span>
                    </Link>
                  </li>
                </ul>
              </div>
            </div>
            <div className={styles.menuTopRight}>
              <h2>{dict.discSpaceUsed}</h2>
              <div className={styles.menuChart}>
                <DiscChart data={data}/>
              </div>
            </div>
          </div>
          <div className={styles.menuBottom}>
            <div className={styles.bottomMenuHeader}>
              <h2>{dict.devicesQuickAccess}:</h2>
              <span>{dict.devicesOnline}: {onlineDevices.length}</span>
            </div>

            <div className={styles.deviceList}>
              {pingLoaded ? (
                onlineDevices.length > 0 ? (
                  <ul className={styles.deviceItems}>
                    {onlineDevices.map((device) => (
                      <li key={device.id} className={styles.deviceItem}>
                        <Link href={`/device/${device.id}`}>
                          <i className="fa-solid fa-video"></i> {device.name}
                        </Link>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <div className={styles.noDevices}>
                    {dict.noDevicesAvailable}
                  </div>
                )
              ) : (
                <div className={styles.loadingDevices}>
                  {dict.loadingDevices}
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
      <ErrorToast
        message={toastMessage}
        show={showToast}
        onHide={() => setShowToast(false)}
      />
    </>
  );
}