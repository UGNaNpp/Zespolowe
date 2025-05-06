import Devices from '../../components/devices/Devices';
import { getDictionary } from "../dictionaries";
import NavBar from '../../components/navbar/NavBar';
import styles from './LoginPageStyle.module.scss';

export default async function LoginPage({ params }: { params: Promise<{ lang: 'en' | 'pl' }> }) {
  const { lang } = await params
  const dict = await getDictionary(lang)

  return(
    <div className={styles.mainScreen}>
      <NavBar title='devices' titleUrl='/devices' subtitle='' subtitleUrl='' />
      <Devices dict={dict.devices} />
    </div>
  );
}
