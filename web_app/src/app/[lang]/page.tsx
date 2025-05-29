"use client";

import NavBar from '../components/navbar/NavBar';
import SendMessageButton from "../components/SendMessageButton";
import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios/axios";

export default function Home() {
    const { data: session, status } = useSession();
    const router = useRouter();
    const [sentToken, setSentToken] = useState(false); // żeby nie wysyłać wielokrotnie

    useEffect(() => {
        const sendTokenToBackend = async () => {
            if (status === "authenticated" && session?.accessToken && !sentToken) {
                try {
                    await api.post(
                        'api/security/verify', 
                        {},
                        {
                            headers: {
                                Authorization: `Bearer ${session.accessToken}`,
                            }
                        }
                    );

                    setSentToken(true);
                    router.push("/");
                } catch (error) {
                    console.error("Błąd przy przesyłaniu tokena do backendu:", error);
                }
            }
        };

        sendTokenToBackend();
    }, [status, session, sentToken, router]);

    return (
        <div>
            <NavBar title='Home' titleUrl='/' subtitle='' subtitleUrl='' />
            <h1>Home</h1>
            <SendMessageButton />
        </div>
    );
}
