import { NextRequest } from 'next/server';

export async function GET(req: NextRequest, { params }: { params: { deviceId: string } }) {
  const backendStreamUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL}${params.deviceId}/stream`;

  const cookie = req.headers.get('cookie') || '';

  const response = await fetch(backendStreamUrl, {
    headers: {
      cookie,
    },
  });

  if (!response.ok || !response.body) {
    return new Response('Stream error', { status: response.status });
  }

  return new Response(response.body, {
    headers: {
      'Content-Type': response.headers.get('Content-Type') ?? 'multipart/x-mixed-replace',
      'Cache-Control': 'no-cache',
    },
  });
}
