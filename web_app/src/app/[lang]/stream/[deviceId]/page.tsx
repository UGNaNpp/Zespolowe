import Stream from "../../../components/stream/Stream";

type Props = {
  params: {
    deviceId: string;
  };
};

export default function StreamPage({ params }: Props) {
  const { deviceId } = params;

  return <Stream deviceId={deviceId} />;
}
