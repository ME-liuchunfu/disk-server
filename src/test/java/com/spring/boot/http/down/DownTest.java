package com.spring.boot.http.down;


import org.junit.Test;

import java.nio.file.Path;

public class DownTest {

    @Test
    public void testDown() {
        // 配置线程池
        ThreadPoolConfig poolConfig = new ThreadPoolConfig()
                .useVirtualThreads(true)
                .corePoolSize(5);

        // 使用try-with-resources自动关闭资源
        try (CustomHeaderDownloadManager manager = new CustomHeaderDownloadManager(poolConfig)) {
            // 设置全局默认请求头（所有任务都会带上）
            manager.setDefaultHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1");
            manager.addDefaultHeader("Accept-Language", "zh-CN,zh;q=0.9");

            // 创建第一个下载任务（带自定义请求头）
            DownloadTaskConfig task1 = new DownloadTaskConfig(
                    "https://p26-sign.douyinpic.com/tos-cn-i-0813/oEDmECEDAwfAHurAGoRdAEhIbAFqe9A326C2Jg~tplv-dy-resize-walign-adapt-aq:540:q75.webp?lk3s=138a59ce&x-expires=1763881200&x-signature=Leckf1BocCmH3AfSUteXG%2FE70mQ%3D&from=327834062&s=PackSourceEnum_DOUYIN_REFLOW&se=false&sc=cover&biz_tag=aweme_video&l=20251109153729CE0766AE23C37709B04E",  // 替换为实际URL
                    Path.of("downloads/cover.png")
            );

            // 添加任务到管理器
            manager.addTask(task1);
            manager.addTask(new DownloadTaskConfig(
                    "https://v11-cold.douyinvod.com/81312925ddc30ef58d9194aaaa1bc192/6910806a/video/tos/cn/tos-cn-ve-15/o8tNadACU3SQLEjBG0PvActxigINpCQUZNiTI/?a=1128&ch=0&cr=0&dr=0&cd=0%7C0%7C0%7C0&cv=1&br=1491&bt=1491&cs=0&ds=3&ft=LXK-HB998xl8uEemg0P5HlvqLGiX2n1TkVJE.K06VbPD-Ipz&mime_type=video_mp4&qs=0&rc=Nzc1ZDMzZTZmMzxpOTkzM0Bpam5xNms5cmdrNjMzNGkzM0AwMGIwXjAuNmAxLjNhLS9fYSM2YmEuMmRrZ2JhLS1kLS9zcw%3D%3D&btag=c0010e0008d000&cquery=100y&dy_q=1762685516&feature_id=fea919893f650a8c49286568590446ef&l=2025110918515667D6D03FF80E5331D7C3",
                    Path.of("downloads/cover.mp4")
            ));

            // 开始下载
            manager.startAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
