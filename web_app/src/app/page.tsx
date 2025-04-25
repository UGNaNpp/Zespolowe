"use client";

import NavBar from './components/navbar/NavBar';
import SendMessageButton from "./components/SendMessageButton";
import { useSession } from "next-auth/react";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
    const { data: session, status } = useSession();
    const router = useRouter();
    const [sentToken, setSentToken] = useState(false); // żeby nie wysyłać wielokrotnie

    useEffect(() => {
        const sendTokenToBackend = async () => {
            if (status === "authenticated" && session?.accessToken && !sentToken) {
                try {
                    const res = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/security/verify`, {
                        method: "POST",
                        headers: {
                            "Authorization": `Bearer ${session.accessToken}`,
                            "Content-Type": "application/json",
                        },
                        credentials: "include",
                    });

                    if (res.ok) {
                        console.log("Token przesłany i ciasteczko ustawione");
                        setSentToken(true);
                        router.push("/");
                    } else {
                        console.error("Błąd przy przesyłaniu tokena do backendu");
                    }
                } catch (error) {
                    console.error("Błąd sieci:", error);
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
