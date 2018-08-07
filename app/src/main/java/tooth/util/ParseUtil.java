package tooth.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class ParseUtil {

    /*
     * 将raw数据中的二进制转换为实际的音频信号 由于默认音频格式为16bit 小端存储 因此这里只使用high和low两位
     * 如果用其他bit宽度需要修改此函数
     *
     * */
    public static int rawAudioDataToShort(byte high, byte low) {
        byte[] byteArray = {high, low};
        return ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    /*
     * 将raw数据中的二进制转换为实际的音频信号 由于默认音频格式为16bit 小端存储 因此这里只使用high和low两位
     * 如果用其他bit宽度需要修改此函数
     *
     * */
    public static List<Byte> shortToRawAudioData(short value) {
        List<Byte> result = new ArrayList<>();
        result.add((byte) (value & 0xff)); // 第0位
        result.add((byte) ((value / 256) & 0xff)); // 第1位
        return result;
    }
}
