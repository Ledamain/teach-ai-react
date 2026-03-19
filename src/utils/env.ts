// 判断是否为浏览器环境（Next.js同构必备）
export const isBrowser = typeof window !== 'undefined' && typeof document !== 'undefined';