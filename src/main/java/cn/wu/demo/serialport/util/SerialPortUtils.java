package cn.wu.demo.serialport.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * 串口工具类
 * @author wusq
 * @date 2021/1/1
 */
public class SerialPortUtils {

    private static Logger log = LoggerFactory.getLogger(SerialPortUtils.class);

    /**
     * 打卡串口
     * @param portName 串口名
     * @param baudRate 波特率
     * @param dataBits 数据位
     * @param stopBits 停止位
     * @param parity 校验位
     * @return 串口对象
     */
    public static SerialPort open(String portName, Integer baudRate, Integer dataBits,
                                      Integer stopBits, Integer parity) {
        SerialPort result = null;
        try {
            // 通过端口名识别端口
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier(portName);
            // 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = identifier.open(portName, 2000);
            // 判断是不是串口
            if (commPort instanceof SerialPort) {
                result = (SerialPort) commPort;
                // 设置一下串口的波特率等参数
                result.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                log.info("打开串口{}成功", portName);
            }else{
                log.info("{}不是串口", portName);
            }
        } catch (Exception e) {
            log.error("打开串口{}错误", portName, e);
        }
        return result;
    }

    /**
     * 串口增加数据可用监听器
     * @param serialPort
     * @param listener
     */
    public static void addListener(SerialPort serialPort, DataAvailableListener listener) {
        if(serialPort == null){
            return;
        }
        try {
            // 给串口添加监听器
            serialPort.addEventListener(new SerialPortListener(listener));
            // 设置当有数据到达时唤醒监听接收线程
            serialPort.notifyOnDataAvailable(Boolean.TRUE);
            // 设置当通信中断时唤醒中断线程
            serialPort.notifyOnBreakInterrupt(Boolean.TRUE);
        } catch (TooManyListenersException e) {
            log.error("串口{}增加数据可用监听器错误", serialPort.getName(), e);
        }
    }

    /**
     * 从串口读取数据
     * @param serialPort
     * @return
     */
    public static byte[] read(SerialPort serialPort) {
        byte[] result = {};
        if(serialPort == null){
            return result;
        }
        InputStream inputStream = null;
        try {
            inputStream = serialPort.getInputStream();

            // 缓冲区大小为1个字节，可根据实际需求修改
            byte[] readBuffer = new byte[7];

            int bytesNum = inputStream.read(readBuffer);
            while (bytesNum > 0) {
                result = ArrayUtil.addAll(result, readBuffer);
                bytesNum = inputStream.read(readBuffer);
            }
        } catch (IOException e) {
            log.error("串口{}读取数据错误", serialPort.getName(), e);
        } finally {
            IoUtil.close(inputStream);
        }
        return result;
    }

    /**
     * 往串口发送数据
     * @param serialPort
     * @param data
     */
    public static void write(SerialPort serialPort, byte[] data) {
        if(serialPort == null){
            return;
        }
        OutputStream outputStream = null;
        try {
            outputStream = serialPort.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        } catch (Exception e) {
            log.error("串口{}发送数据错误", serialPort.getName(), e);
        } finally {
            IoUtil.close(outputStream);
        }
    }

    /**
     * 关闭串口
     * @param serialPort
     */
    public static void close(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            log.warn("串口{}关闭", serialPort.getName());
        }
    }

    /**
     * 查询可用端口
     * @return 串口名List
     */
    public static List<String> listPortName() {
        List<String> result = new ArrayList<>();

        // 获得当前所有可用端口
        Enumeration<CommPortIdentifier> serialPorts = CommPortIdentifier.getPortIdentifiers();
        if(serialPorts == null){
            return result;
        }

        // 将可用端口名添加到List并返回该List
        while (serialPorts.hasMoreElements()) {
            result.add(serialPorts.nextElement().getName());
        }
        return result;
    }
}
