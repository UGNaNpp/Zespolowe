"use client";

import NavBar from './components/navbar/NavBar';
import SendMessageButton from "@/app/components/SendMessageButton";

export default function Home() {
  return (
    <div>
      <NavBar title='Home' titleUrl='/' subtitle='' subtitleUrl=''/>
      <h1>Home</h1>
        <SendMessageButton />
    </div>
  );
}
