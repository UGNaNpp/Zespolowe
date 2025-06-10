"use client";

import { useFormik } from "formik";
import * as Yup from "yup";
import { Device } from "@/types/device";
import styles from "@/app/components/deviceForm/deviceFormStyle.module.scss";
import { apiRequest } from "@/app/api/axios/apiRequest";
import { useState } from "react";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";
import { useRouter } from "next/navigation";

type Props = {
  device?: Device;
  deviceId?: string;
  modify: boolean;
  dict: {
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
  },
  onClose: () => void;
  successPostAction?: () => void;
};

export function DeviceEditForm({ device, deviceId, modify, dict, ApiErrorsDict, onClose, successPostAction }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState<string>("");
  const router = useRouter();

  const validationSchema = Yup.object({
    name: Yup.string()
      .required(dict.required)
      .max(20, dict.maxName),
    AssociatedIP: Yup.string()
      .required(dict.required)
      .matches(
        /^(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}$/,
        dict.invalidIP
    ),
    AssociatedMAC: Yup.string()
      .required(dict.required)
      .matches(
        /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/,
        dict.invalidMAC
      ),
    widthResolution: Yup.number()
      .required(dict.required)
      .positive()
      .positive(dict.widthPositive)
      .max(3840, dict.widthMax),
    heightResolution: Yup.number()
      .required(dict.required)
      .positive(dict.heightPositive)
      .max(2160, dict.heightMax),
    recordingMode: Yup.boolean(),
    recordingVideo: Yup.boolean(),
  });

  const formik = useFormik({
    initialValues: {
      name: device?.name || "",
      AssociatedIP: device?.AssociatedIP || "",
      AssociatedMAC: device?.AssociatedMAC || "",
      widthResolution: device?.widthResolution || "",
      heightResolution: device?.heightResolution || "",
      recordingMode: device?.recordingMode || false,
      recordingVideo: device?.recordingVideo || false,
    },
    validationSchema,
    onSubmit: (values) => {
      apiRequest({
        endpoint: modify ? `/devices/edit/id/${deviceId}` : `/devices/`,
        method: modify ? "PUT" : "POST",
        data: values,
        onSuccess: () => {
          setToastMessage(modify ? dict.updated : dict.added);
          setShowToast(true);
          if (successPostAction) {
            successPostAction();
          } else {
            setTimeout(() => {
              router.push("/devices");
            }, 2000);
          }
        },
        onError: (errMsg) => {
          setToastMessage(errMsg);
          setShowToast(true);
        },
        dict: ApiErrorsDict,
      });
    },
  });

  return (
    <div className={styles.menu}>
      <div className={styles.h2andclose}>
        <h2>{modify ? dict.modify : dict.addNew}</h2>
        <button type="button" onClick={onClose} className={styles.closeButton}>
          <i className="fa-solid fa-xmark fa-xl"></i>
        </button>
      </div>
      <form className={styles.form} onSubmit={formik.handleSubmit}>
        <label>
          {dict.name}
          <input
            type="text"
            name="name"
            value={formik.values.name}
            onChange={formik.handleChange}
          />
          {formik.touched.name && formik.errors.name && <div className={styles.error}>{formik.errors.name}</div>}
        </label>

        <label>
          {dict.ip}
          <input
            type="text"
            name="AssociatedIP"
            value={formik.values.AssociatedIP}
            onChange={formik.handleChange}
          />
          {formik.touched.AssociatedIP && formik.errors.AssociatedIP && <div className={styles.error}>{formik.errors.AssociatedIP}</div>}
        </label>

        <label>
          {dict.mac}
          <input
            type="text"
            name="AssociatedMAC"
            value={formik.values.AssociatedMAC}
            onChange={formik.handleChange}
          />
          {formik.touched.AssociatedMAC && formik.errors.AssociatedMAC && <div className={styles.error}>{formik.errors.AssociatedMAC}</div>}
        </label>

        <label>
          {dict.resolution}
          <div className={styles.resolutionMenu}>
            <div className={styles.resolutionInputs}>
              <input
                type="number"
                name="widthResolution"
                value={formik.values.widthResolution}
                onChange={formik.handleChange}
              />
              x
              <input
                type="number"
                name="heightResolution"
                value={formik.values.heightResolution}
                onChange={formik.handleChange}
              />
            </div>
            <div>px</div>
          </div>
          {formik.touched.widthResolution && formik.errors.widthResolution && <div className={styles.error}>{formik.errors.widthResolution}</div>}
          {formik.touched.heightResolution && formik.errors.heightResolution && <div className={styles.error}>{formik.errors.heightResolution}</div>}
        </label>

        <label>
          <span>
          {dict.recordingMode}
            <input
              type="checkbox"
              name="recordingMode"
              checked={formik.values.recordingMode}
              onChange={formik.handleChange}
            />
          </span>
        </label>

        <label>
          <span>
            {dict.recordingVideo}
            <input
              type="checkbox"
              name="recordingVideo"
              checked={formik.values.recordingVideo}
              onChange={formik.handleChange}
            />
          </span>
        </label>

        <button type="submit">{modify ? dict.save : dict.add}</button>

        <ErrorToast
          message={toastMessage}
          show={showToast}
          onHide={() => setShowToast(false)}
        />
      </form>
    </div>
  );
}
