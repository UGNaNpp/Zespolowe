'use client';

import { useEffect, useState } from "react";
import styles from '@/app/components/media/mediaStyle.module.scss';
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { Device } from "@/types/device";
import api from "@/app/api/axios/axios";

type Props = {
  dict: {
    "pageTitle": string;
    "recordNotFound": string;
    "recordDateMismatch": string;
    "recordDateInFuture": string;
    "recordingSub1": string;
    "recordingSub2": string;
    "recordingStarted": string;
    "downloadingError": string;
    "unknownError": string;
    "recordingNotFound": string;
    "missingInput": string;
    "controlsMenuTitle": string;
    "date": string;
    "devices": string;
    "fps": string;
    "time": string;
    "chooseAll": string;
    "play": string;
    "goBack": string;
    "recording": string;
    "recordingStart": string;
    "recordingStop": string;
    "exportFPS": string;
    "saveRecording": string;
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

export default function Media({ dict, ApiErrorsDict }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [devices, setDevices] = useState<Device[]>([]);
  const [dates, setDates] = useState<string[]>([]);
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [filteredDevices, setFilteredDevices] = useState<Device[]>([]);
  const [selectedDevice, setSelectedDevice] = useState<string>("");
  const [selectedFps, setSelectedFps] = useState<string>("30");
  const [selectedTime, setSelectedTime] = useState<string>("00:00:00");
  const [showVideo, setShowVideo] = useState(false);
  const [exportStartTime, setExportStartTime] = useState<string>("00:00:00");
  const [exportStopTime, setExportStopTime] = useState<string>("00:05:00");
  const [exportFps, setExportFps] = useState<string>("30");
  const [streamKey, setStreamKey] = useState<number>(Date.now());
  const [videoError, setVideoError] = useState(false);

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

    apiRequest<string[]>({
      endpoint: "/records/available-days",
      method: "GET",
      onSuccess: (data) => {
        setDates(Object.values(data));
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

  useEffect(() => {
    if (selectedDate) {
      apiRequest<string[]>({
        endpoint: `/records/available-cameras-by-date/${selectedDate}`,
        method: "GET",
        onSuccess: (ids) => {
          const matchedDevices = devices.filter(device => ids.includes(device.id.toString()));
          setFilteredDevices(matchedDevices);
        },
        onError: (errMsg) => {
          setError(errMsg);
          setToastMessage(errMsg);
          setShowToast(true);
        },
        dict: ApiErrorsDict,
      });
    } else {
      setFilteredDevices([]);
    }
  }, [selectedDate, devices, ApiErrorsDict]);

  const handleVideoError = () => {
    setVideoError(true);
  };

  const handleExport = () => {
  if (exportStartTime && exportStopTime) {
    const formattedStart = `${selectedDate}T${exportStartTime}`;
    const formattedStop = `${selectedDate}T${exportStopTime}`;

    const startDate = new Date(formattedStart);
    const stopDate = new Date(formattedStop);
    const now = new Date();

    if (stopDate <= startDate) {
      setToastMessage(dict.recordDateMismatch);
      setShowToast(true);
      return;
    }

    if (stopDate > now) {
      setToastMessage(dict.recordDateInFuture);
      setShowToast(true);
      return;
    }

    setToastMessage(`${dict.recordingSub1} ${formattedStart} ${dict.recordingSub2} ${formattedStop} (${exportFps} FPS).`);
    setShowToast(true);

    apiRequest({
      endpoint: `/records/video/save?cameraId=${selectedDevice}&startDateTime=${formattedStart}&stopDateTime=${formattedStop}&frameRate=${exportFps}`,
      method: "POST",
      onSuccess: async (data) => {
        const recordName = data;
        try {
          const response = await api.get(`/records/video/download/${recordName}`, {
            responseType: "blob",
          });

          const blob = response.data;
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement("a");
          link.href = url;
          link.download = `${data}`;
          document.body.appendChild(link);
          link.click();
          link.remove();
          window.URL.revokeObjectURL(url);
          setToastMessage("Pobieranie nagrania rozpoczęte.");
          setShowToast(true);

        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        } catch (err: any) {
          setToastMessage(`${dict.downloadingError}: ${err.message || dict.unknownError}`);
          setShowToast(true);
        }
      },
      onError: () => {
        setToastMessage(dict.recordingNotFound);
        setShowToast(true);
      },
      dict: ApiErrorsDict,
    });

  } else {
    setToastMessage(dict.missingInput);
    setShowToast(true);
  }
};

  return (
    <>
      <main className={styles.main}>
        {!loading && !error && (
          <>
            {!showVideo ? (
              <div className={styles.controlsMenu}>
                <h2>{dict.controlsMenuTitle}</h2>
                <div className={styles.controls}>
                  <div className={styles.controlsTop}>
                    <label>
                      {dict.date}:
                      <select
                        value={selectedDate || ""}
                        onChange={(e) => {
                          setSelectedDate(e.target.value);
                          setSelectedDevice("");
                        }}
                      >
                        <option value="" disabled></option>
                        {dates.map((date) => (
                          <option key={date} value={date}>
                            {date}
                          </option>
                        ))}
                      </select>
                    </label>
                    <label>
                      {dict.devices}:
                      <select
                        value={selectedDevice}
                        onChange={(e) => setSelectedDevice(e.target.value)}
                        disabled={!selectedDate}
                      >
                        <option value="" disabled></option>
                        {filteredDevices.map((device) => (
                          <option key={device.id} value={device.id}>
                            {device.name}
                          </option>
                        ))}
                      </select>
                    </label>
                    <label className={styles.fps}>
                      {dict.fps}: {selectedFps.padStart(2, '0')}
                      <input
                        type="range"
                        min="1"
                        max="60"
                        step="1"
                        value={selectedFps}
                        onChange={(e) => setSelectedFps(e.target.value)}
                      />
                    </label>
                  </div>
                  <div className={styles.controlsBottom}>
                    <label>
                      {dict.time}:
                      <input
                        type="time"
                        step="1"
                        value={selectedTime}
                        onChange={(e) => setSelectedTime(e.target.value)}
                      />
                    </label>
                    <div>
                      <button
                        onClick={() => {
                          if (selectedDevice && selectedDate && selectedTime) {
                            const [hours, minutes, seconds] = selectedTime.split(':').map(Number);
                            const date = new Date();
                            date.setHours(hours, minutes + 5, seconds);
                            const pad = (n: number) => n.toString().padStart(2, '0');
                            const newStopTime = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;

                            setExportStartTime(selectedTime);
                            setExportStopTime(newStopTime);
                            setVideoError(false);
                            setStreamKey(Date.now());
                            setShowVideo(true);
                          } else {
                            setToastMessage(dict.chooseAll);
                            setShowToast(true);
                          }
                        }}
                      >
                        {dict.play}
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className={styles.videoMenu}>
                <button
                  className={styles.backButton}
                  onClick={() => {
                    setShowVideo(false);
                    setVideoError(false);
                  }}
                >
                  <i className="fa-solid fa-arrow-left"></i>
                  {dict.goBack}
                </button>
                <h2>{dict.recording} {selectedDate} {selectedTime}</h2>
                <div className={styles.streamBox}>
                  {videoError ? (
                    <span>{dict.recordNotFound}</span>
                  ) : (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img
                      key={streamKey}
                      src={`/api/recordstream?id=${selectedDevice}&date=${selectedDate}&time=${selectedTime}&fps=${selectedFps}&t=${streamKey}`}
                      alt=""
                      className={styles.stream}
                      onError={handleVideoError}
                    />
                  )}
                </div>
                <div className={styles.videoControlls}>
                  <div className={styles.controllsTime}>
                    <label>
                      {dict.recordingStart}:
                      <input
                        type="time"
                        step="1"
                        value={exportStartTime}
                        onChange={(e) => setExportStartTime(e.target.value)}
                      />
                    </label>
                    <label>
                      {dict.recordingStop}:
                      <input
                        type="time"
                        step="1"
                        value={exportStopTime}
                        onChange={(e) => setExportStopTime(e.target.value)}
                      />
                    </label>
                  </div>
                  <label className={styles.fps}>
                    {dict.exportFPS}: {exportFps.padStart(2, '0')}
                    <input
                      type="range"
                      min="1"
                      max="60"
                      step="1"
                      value={exportFps}
                      onChange={(e) => setExportFps(e.target.value)}
                    />
                  </label>
                  <button className={styles.downloadButton} onClick={handleExport}>
                    {dict.saveRecording}
                  </button>
                </div>
              </div>
            )}
          </>
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
