package com.example.backend.controllers;

import com.example.backend.effectutils.DataEffectSkill;
import com.example.backend.response.ParseMapRequest;
import com.example.backend.response.UploadMapResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController("/")
@CrossOrigin("*")
@Slf4j
public class Index {

    @PostMapping("/uploadMapFile")
    public UploadMapResponse uploadMap(@RequestParam("file") MultipartFile file) {
        var response = new UploadMapResponse();
        try (var dis = new DataInputStream(file.getInputStream())) {
            var width = dis.readByte();
            var height = dis.readByte();
            short[][] rows = new short[height][width];
            for (int i = 0; i < rows.length; i++) {
                for (int j = 0; j < rows[i].length; j++) {
                    int v = dis.readByte();
                    if (v < 0) {
                        v += 256;
                    }
                    rows[i][j] = (short) (v - 1);
                }
            }
            response.setWidth(width);
            response.setHeight(height);
            response.setMaps(rows);
            log.info("Upload file success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    @PostMapping(value = "/uploadMapData", consumes = "application/json")
    public ResponseEntity<?> uploadMapData(@RequestBody ParseMapRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=" + request.fileName);
        try (
                var bas = new ByteArrayOutputStream();
                var dos = new DataOutputStream(bas);
        ) {

            dos.writeByte(request.width);
            dos.writeByte(request.height);
            for (var row : request.maps) {
                for (short v : row) {
                    dos.writeByte(v + 1);
                }
            }
            dos.flush();
            response.setContentType("application/octet-stream");
            return ResponseEntity.ok().contentLength(dos.size()).body(new InputStreamResource( new ByteArrayInputStream(bas.toByteArray())));
        }
    }

    @RequestMapping(path = "/uploadEffData", method = RequestMethod.POST)
    public ResponseEntity<DataEffectSkill> uploadData(@RequestParam("file") MultipartFile file) throws IOException {
        try(var dis = new DataInputStream(file.getInputStream())){
            var data = new DataEffectSkill();
            data.readEffectData(dis);
            return ResponseEntity.ok(data);
        }
    }

}
