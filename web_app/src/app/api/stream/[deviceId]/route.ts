import { NextRequest, NextResponse } from 'next/server';

export async function GET(req: NextRequest) {
  const { pathname } = req.nextUrl;
  const parts = pathname.split('/');
  const deviceId = parts[parts.length - 1]

  const backendStreamUrl = `${process.env.NEXT_PUBLIC_BACKEND_URL}${deviceId}/stream`;

  const cookie = req.headers.get('cookie') || '';

  const response = await fetch(backendStreamUrl, {
    headers: {
      cookie,
    },
  });

  if (!response.ok || !response.body) {
    return new NextResponse('Stream error', { status: response.status });
  }

  return new NextResponse(response.body, {
    headers: {
      'Content-Type': response.headers.get('Content-Type') ?? 'multipart/x-mixed-replace',
      'Cache-Control': 'no-cache',
    },
  });
}
