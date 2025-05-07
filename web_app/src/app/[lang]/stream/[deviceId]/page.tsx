import Stream from "../../../components/stream/Stream";

type Props = {
  params: Promise<{ deviceId: string }>;
};

export default async function StreamPage({ params }: Props) {
  const { deviceId } = await params;

  return <Stream deviceId={deviceId} />;
}
