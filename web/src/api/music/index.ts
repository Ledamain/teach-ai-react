import { request } from '@/utils/request'; // 按你项目路径改

/**
 * 获取网易云推荐歌单
 * @param limit 数量 默认10
 * @param offset 分页偏移 默认0
 */
export function getMusicRecommendPlaylist(limit = 30, offset = 0) {
    // 业务参数
    const bizContent = JSON.stringify({
        limit: limit.toString(),
        offset: offset.toString(),
    });

    // 设备参数
    const device = JSON.stringify({
        deviceType: 'andrwear',
        os: 'otos',
        appVer: '0.1',
        channel: 'hm',
        model: 'kys',
        deviceId: '357',
        brand: 'hm',
        osVer: '8.1.0',
    });

    // 接口完整参数
    return request.get('/openapi/music/basic/recommend/playlist/get', {
        params: {
            appId: 'a301010000000000aadb4e5a28b45a67',
            signType: 'RSA_SHA256',
            accessToken: '9ffc6030fb9b8d186a33d45d32779638907ef86e8d889918bd',
            timestamp: Date.now().toString(),
            bizContent,
            device,
            sign: 'MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDTTt7pr2MyKorU9AQqYlj1Md+aset1LMskVRT1b5EPpm402nmwMoBcD/q67akYNZOV4+bY3iqMC6XzpQdj/PkB/Apfd3gvpAiTxxxbioklH9fCDQV9g1dxqwrO5WiXJ4Y4xz7iYUJYZCTgnlfqyS9sF82PA0nwa1P2pn7rSseXzKTgyD0mmW9tJapI3XZMxblTNT0dUk5lUThOZf4+0xrxK15nVCWRkRHwbff+4GL3haUeHMD/oWqaFMb7tPFS11u5+AUfE2vTExlOpEWRBIWblcHerjXMyzJO8Q9f/6FiMZCQCU8szt5Kej22laxfAICqCyqQm7LSTsz5oeCCWk+9AgMBAAECggEABiNfotKrM0EcnNCE0XVfZ6n9+JisAhCdyw2kqPeHc3iucV8aU1W4OFRngGgYdiFSSvOemFSl0EEjXjdARnWKBzZ616iZB2tAq4hIaNlWoAszgYnOy8VVtDHXg8iSt/dYNQyGpV1VZ8tq7dJzs9iJxd5QJJQgIPmBLCRMggm/UAe+iFYsQsKsfHD7VEkQveDm59Tep+w248VNW0PsmI5SpJKrTHO84RJNx0bP9n7K8woLSft2V+to3tsGKktH1vaPGG+AegD0xdFPoZGm5EEzs9gp4s7H58KJcCl/vLw9gcGNGRlsYb51T63yCLZBvgWhxI4Td2gyJqXqL1l/gjTFeQKBgQDzxgfUyL08Sa9EmcTVMZKEz4XgbOojUVs2Mia0CXMdlTYWwjUzfBn07o6W3JbH8Gmre+l9PtCise3qx3qyeFy6R1SYOIj/MsPGnLKsMrmSNq+8NA1q2tiQJGKJJh8ujUgkYdVZYRZkISwTSN7OY1vDm3setKlNtKotb5ixjEl2KQKBgQDd5/6fCcpql68KYCRbuf3QvSJwDBmphEk6nOYNmSFMnEtb3dzow4OSdRGbHfM3EiMGPJtedi75BT7TsIUgzMbNBG0LI5mMfiSRaJXLRREZpSBrQ3dQM2jXQXV+F7laH4CFVwu425N9eJNJ+X3stCe98H7l+V/QNavFy11H4NC3dQKBgAxbS2ZTzpPRwFqf2MRdwOmOg2C+5Jdptk/m9uNFX1puh9FbgsrbTY2G0h9/iv0TiKeynkFE/9pfXon6FpQZWcnvd7BHnVoiPp5AryXfZFyu/PVzRgLWmQelcIE3N3lqm++MLf5b14C31b4cgX7AtpQRu4nw6mwP7lhjQcAuNkMRAoGAQD3yo940wIkrPqi4velphq3Ks399SmsmvBZCL7uePUyeMHlZg/VeON81ep/AErhodVzEaiNy09rl5mkbEXTXNPFMFEZ5JIg8U/BgNVnIapE4gnUkrI5FktO7jQaXPXUTQwcBOMIwv8lRcsAM6aL6Qm3GfxOj3mY7VWbbmXyHUCUCgYAHzyKpvudoMYp1hCXzva+tHO/uKbRmK4wa3IX3IlR63eBT6DJPn+KLzZZ3zfG0sqafOXHuU88PoM+PHmn7M/KsMool/qwMKh/zDJIfX4+E4v08tCScn8gMfmWptvLDx611H/POpu4lC3wVqUXR1ZjW3+rMVSHWVvjZrtqhaYkSjA=='
            // sign: 后端签名后传入 必须加！
        },
        // 覆盖 baseURL 直接调用网易云接口
        baseURL: 'http://openapi.music.163.com',
    });
}