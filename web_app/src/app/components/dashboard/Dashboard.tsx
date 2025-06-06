import styles from '@/app/components/dashboard/dashboardStyle.module.scss';

type Props = {
  dict: {
    pageTitle: string;
  };
};

export default function Dashboard({ dict }: Props) {
  return(
    <main className={styles.main}>
      <h1>Dashboard</h1>
    </main>
  );
}