"use client";

import { useFormik } from "formik";
import * as Yup from "yup";
import { useState } from "react";
import { apiRequest } from "@/app/api/axios/apiRequest";
import styles from "@/app/components/addUserForm/addUserFormStyle.module.scss";
import { ErrorToast } from "@/app/components/errorToast/ErrorToast";

type Props = {
  dict: {
    "addUser": string;
    "login": string;
    "add": string;
    "cancel": string;
    "addedUser": string;
    "notFound": string;
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
};

export function UserAddForm({ dict, ApiErrorsDict, onClose }: Props) {
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");

  const validationSchema = Yup.object({
    login: Yup.string()
      .required("Login jest wymagany")
      .test("not-empty", "Login nie może zawierać tylko spacji", (value) =>
        !!value?.trim()
      ),
  });

  const formik = useFormik({
    initialValues: {
      login: "",
    },
    validationSchema,
    onSubmit: (values) => {
      apiRequest({
        endpoint: '/users/add-user-by-login',
        method: "POST",
        headers: {
          "Content-Type": "text/plain",
        },
        data: values.login,
        onSuccess: () => {
          setToastMessage(dict.addedUser);
          setShowToast(true);
          setTimeout(() => {
            setShowToast(false);
            onClose();
          }, 1500);
        },
        onError: () => {
          setToastMessage(dict.notFound);
          setShowToast(true);
        },
        dict: ApiErrorsDict,
      });
    },
  });

  return (
    <div className={styles.menu}>
      <div className={styles.h2andclose}>
        <h2>{dict.addUser}</h2>
      </div>
      <form className={styles.form} onSubmit={formik.handleSubmit}>
        <label>
          {dict.login}
          <input
            type="text"
            name="login"
            value={formik.values.login}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
        </label>
        {formik.touched.login && formik.errors.login && (
          <div className={styles.error}>{formik.errors.login}</div>
        )}

        <div className={styles.buttonGroup}>
          <button type="submit">{dict.add}</button>
          <button type="button" onClick={onClose}>
            {dict.cancel}
          </button>
        </div>
      </form>

      <ErrorToast
        message={toastMessage}
        show={showToast}
        onHide={() => setShowToast(false)}
      />
    </div>
  );
}