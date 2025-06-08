import Device from '@/app/components/device/Device';
import { getDictionary } from "@/app/[lang]/dictionaries";
import NavBar from '@/app/components/navbar/NavBar';
import styles from '@/app/[lang]/device/[deviceId]/devicePageStyle.module.scss';
import type { Metadata } from 'next';
import type { Lang } from '@/types/routeParams';

type DeviceParams = {
  lang: Lang;
  deviceId: string;
};

export type DeviceProps = {
  params: Promise<DeviceParams>;
};


export async function generateMetadata({ params }: DeviceProps): Promise<Metadata> {
  const resolvedParams = await params;
  const dict = await getDictionary(resolvedParams.lang);

  return {
    title: dict.device.pageTitle
  };
}

export default async function DevicePage({ params }: DeviceProps) {
  const { lang, deviceId } = await params
  const dict = await getDictionary(lang)

  return(
    <main className={styles.main}>
      <NavBar title='Devices' titleUrl='/devices' subtitle={deviceId} subtitleUrl='' dict={dict.navBar} />
      <Device deviceId={deviceId} dict={dict.device} ApiErrorsDict={dict.apiErrors} />
    </main>
  );
}
