"use client";

export default function Stream({ deviceId }: { deviceId: string }) {
  const streamUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL}${deviceId}/stream`;

  return (
    <div style={{ width: "100%", height: "100vh", backgroundColor: "black" }}>
      <img
        src={streamUrl}
        alt="Stream"
        style={{ width: "100%", height: "100%", objectFit: "contain" }}
      />
    </div>
  );
}
