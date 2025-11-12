package com.spring.boot.disk.server.mapstruct;

import com.spring.boot.disk.server.entity.po.VmDiskFile;
import com.spring.boot.disk.server.entity.po.VmUserFile;
import com.spring.boot.disk.server.model.resp.DiskDirScanResponse;
import com.spring.boot.disk.server.model.resp.DiskFileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * description: TODO
 *
 * @author spring
 * date 2025/11/12 15:32
 * version 1.0
 */
@Mapper
public interface ConvertFactory {

    ConvertFactory INST = Mappers.getMapper(ConvertFactory.class);

    DiskDirScanResponse toDiskDirScanResponse(VmUserFile vmUserFile);

    DiskFileInfo toDiskFileInfo(VmDiskFile vmDiskFile);

}
