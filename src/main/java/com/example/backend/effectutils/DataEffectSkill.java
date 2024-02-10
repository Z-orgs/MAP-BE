package com.example.backend.effectutils;

import com.example.backend.effectmodels.FrameEff;
import com.example.backend.effectmodels.PartFrame;
import com.example.backend.effectmodels.SmallImage;
import lombok.SneakyThrows;
import lombok.val;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataEffectSkill {

    public SmallImage[] imgs;
    public List<FrameEff> listFrame;
    public int[] sequence;
    public int[][] frameChar = new int[4][];


    /**
     * @param dis is the DataInputStream
     * @return the first item is List
     */
    @SneakyThrows
    public void readEffectData(DataInputStream dis) {
        try (dis) {
            dis.readByte();
            dis.readUnsignedByte();
            dis.readShort();
            this.imgs = new SmallImage[dis.readByte()];
            for (int i = 0; i < imgs.length; i++) {
                imgs[i] = new SmallImage(dis.readUnsignedByte(), dis.readUnsignedByte(), dis.readUnsignedByte(), dis.readUnsignedByte(), dis.readUnsignedByte());
            }
            var num2 = 0;
            var frame = dis.readShort();
            listFrame = new ArrayList<>();
            for (int i = 0; i < frame; i++) {
                val nPart = dis.readByte();
                List<PartFrame> topFrames = new ArrayList<>();
                List<PartFrame> bottomFrames = new ArrayList<>();

                for (int j = 0; j < nPart; j++) {
                    val partFrame = new PartFrame(dis.readShort(), dis.readShort(), dis.readByte());
                    partFrame.flip = dis.readByte();
                    partFrame.onTop = dis.readByte();
                    if (partFrame.onTop == 0) {
                        topFrames.add(partFrame);
                    } else {
                        bottomFrames.add(partFrame);
                    }

                    if (num2 < Math.abs(partFrame.dy)) {
                        num2 = Math.abs(partFrame.dy);
                    }
                }
                listFrame.add(new FrameEff(topFrames, bottomFrames));
            }
            short num4 = (short) dis.readUnsignedByte();
            sequence = new int[num4];
            for (int i = 0; i < num4; i++) {
                sequence[i] = (byte) dis.readShort();
            }
            dis.readByte();
            num4 = dis.readByte();
            frameChar[0] = new int[num4];
            for (int i = 0; i < num4; i++) {
                frameChar[0][i] = dis.readByte();
            }
            num4 = dis.readByte();
            frameChar[1] = new int[num4];
            for (int i = 0; i < num4; i++) {
                frameChar[1][i] = dis.readByte();
            }
            num4 = dis.readByte();
            frameChar[3] = new int[num4];
            for (int i = 0; i < num4; i++) {
                frameChar[3][i] = dis.readByte();
            }
        }
    }

    @Override
    public String toString() {
        return "DataEffectSkill{" +
                "imgs=" + Arrays.toString(imgs) +
                ", listFrame=" + listFrame +
                ", sequence=" + Arrays.toString(sequence) +
                ", frameChar=" + Arrays.toString(frameChar) +
                '}';
    }
}
