import { NextRequest, NextResponse } from 'next/server';

export async function GET(req: NextRequest) {
  const { searchParams } = new URL(req.url);

  const deviceId = searchParams.get("id");
  const date = searchParams.get("date");
  const time = searchParams.get("time");
  const fps = searchParams.get("fps");

  if (!deviceId || !date || !time || !fps) {
    return new NextResponse("Missing query parameters", { status: 400 });
  }

  const cookie = req.headers.get('cookie') || '';

  const backendUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL}records/stream/${deviceId}/${date}T${time}/${fps}`;

  console.log("[VIDEO STREAM] Proxying request to:", backendUrl);

  const response = await fetch(backendUrl, {
    headers: {
      cookie,
    },
  });

  if (!response.ok || !response.body) {
    return new NextResponse('Stream error', { status: response.status });
  }

  return new NextResponse(response.body, {
    headers: {
      'Content-Type': response.headers.get('Content-Type') ?? 'multipart/x-mixed-replace; boundary=frame',
      'Cache-Control': 'no-cache',
    },
  });
}
