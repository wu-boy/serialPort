package cn.wu.demo.serialport.util;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 串口监听器
 * @author wusq
 * @date 2021/1/1
 */
public class SerialPortListener implements SerialPortEventListener {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DataAvailableListener listener;

    public SerialPortListener(DataAvailableListener listener) {
        this.listener = listener;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                // 串口存在有效数据
                if (listener != null) {
                    listener.dataAvailable();
                }
                break;
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                // 输出缓冲区已清空
                break;
            case SerialPortEvent.CTS:
                // 清除待发送数据
                break;
            case SerialPortEvent.DSR:
                // 待发送数据准备好了
                break;
            case SerialPortEvent.RI:
                // 振铃指示
                break;
            case SerialPortEvent.CD:
                // 载波检测
                break;
            case SerialPortEvent.OE:
                // 溢位（溢出）错误
                log.error("溢位（溢出）错误");
                break;
            case SerialPortEvent.PE:
                // 奇偶校验错误
                log.error("奇偶校验错误");
                break;
            case SerialPortEvent.FE:
                // 帧错误
                log.error("帧错误");
                break;
            case SerialPortEvent.BI:
                // 通信中断
                log.error("串口通信中断");
                break;
            default:
                break;
        }
    }
}
