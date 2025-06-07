"use client";

import { useSession } from "next-auth/react";
import { useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios/axios";

// DODAC TUTAJ ZE JAK 401 - PRZEKIEROWANIE NA STRONE NA KTOREJ NAPIS ZE NIE MASZ DOSTEPU DO APLIKACJI

export default function HomePage() {
  const { data: session, status } = useSession();
  const router = useRouter();
  const sentTokenRef = useRef(false);

  useEffect(() => {
    if (status !== "authenticated" || sentTokenRef.current || !session?.accessToken) return;

    const sendTokenToBackend = async () => {
      try {
        await api.post(
          "api/security/verify",
          {},
          {
            headers: {
              Authorization: `Bearer ${session.accessToken}`,
            },
          }
        );

        sentTokenRef.current = true;
        router.push("/dashboard");
      } catch (error) {
        console.error("Błąd przy przesyłaniu tokena do backendu:", error);
      }
    };

    sendTokenToBackend();
  }, [status, session?.accessToken, router]);

  return null;
}
