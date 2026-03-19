import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  reactStrictMode: true,
  async rewrites() {
    return [
      {
        source: '/client-api/:path*', // 前端请求的/api开头路径
        destination: `${process.env.NEXT_PUBLIC_API_URL_LOC}/client-api/client-system/:path*` // 转发到真实后端地址
      }
    ];
  }
};

export default nextConfig;
