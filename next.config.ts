import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    // 配置允许加载的外部图片域名
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'teach-ai.tos-cn-beijing.volces.com',
        port: '',
        pathname: '/**',
      },
    ],
  },
  /* config options here */
  typescript: {
    ignoreBuildErrors: true,
  },
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
