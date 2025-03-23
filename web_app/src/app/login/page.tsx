import styles from './loginStyle.module.scss';

export default async function Login() {
  const loginTitle = process.env.LOGIN_TITLE;

  return (
    <div className={styles.background}>
      <div className={styles.mainForm}>
        <h2>{loginTitle}</h2>
        <div className={styles.buttonDiv}>
          <button>
            <i className="fa-brands fa-github"></i>
            <span>Login with github</span>
          </button>
        </div>
      </div>
    </div>
  );
}
