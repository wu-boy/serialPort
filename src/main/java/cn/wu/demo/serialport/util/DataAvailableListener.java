package cn.wu.demo.serialport.util;

/**
 * 串口数据可用监听器
 * @author wusq
 * @date 2021/1/1
 */
public interface DataAvailableListener {

    /**
     * 串口存在有效数据时调用
     */
    void dataAvailable();
}
