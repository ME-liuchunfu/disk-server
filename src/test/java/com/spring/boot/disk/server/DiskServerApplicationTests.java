package com.spring.boot.disk.server;

import com.spring.boot.disk.server.entity.po.VmDiskFile;
import com.spring.boot.disk.server.service.VmDiskFileService;
import com.spring.boot.utils.file.FileInfo;
import com.spring.boot.utils.file.FileInfoUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootTest
class DiskServerApplicationTests {

	@Resource
	VmDiskFileService vmDiskFileService;

	@Test
	void contextLoads() {

	}

	@Test
	void scanerDiskFile() throws IOException {
		String dir = "E:\\temp_svn_doc";
		List<FileInfo> allFileInfos = FileInfoUtil.getAllFileInfos(dir);
		int size = allFileInfos.size();
		int index = 0;
		for (FileInfo info : allFileInfos) {
			index++;
			log.info("正在处理: {}/{} {}", index, size, info.getFileName());
			VmDiskFile vmDiskFile = new VmDiskFile();
			vmDiskFile.setHashValue(info.getHashValue());
			vmDiskFile.setFileName(info.getFileName());
			vmDiskFile.setFileSize(info.getFileSize());
			vmDiskFile.setFileType(info.getFileType());
			vmDiskFile.setPath(info.getRelativePath());
			long count = vmDiskFileService.countUnKey(vmDiskFile);
			if (count > 0) {
				log.error("文件存在数据库中:{}, path:{}", info.getHashValue(), info.getRelativePath());
				continue;
			}
			vmDiskFileService.insert(vmDiskFile);
		}
	}

}
