import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* Toàn bộ trang phụ thuộc session đăng nhập (cookies) nên tắt Cache
     Components/PPR — không có trang nào hưởng lợi từ prerender tĩnh. */
  turbopack: {
    rules: {
      "*.css": {
        loaders: ["@tailwindcss/turbopack"],
        as: "*.css",
      },
    },
  },
};

export default nextConfig;
