package com.example.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.pkts.PacketHandler;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

@SpringBootApplication
@RestController

public class JavaPcapFileRunApplication {
	String mPath=null;

	public static void main(String[] args) {
		SpringApplication.run(JavaPcapFileRunApplication.class, args);
	}
	@GetMapping("/")
	public String getMessage()  {
		 return "Home Page";
	 }
		

	@GetMapping("/test-file")
	public String rundefaultpcap(){
		
		try {
			URL res=getClass().getClassLoader().getResource("test.pcap");
			File file =Paths.get(res.toURI()).toFile();
			
			
			System.out.println("path=>"+file.getAbsolutePath());
			runPcap(file.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Testing is working";
	}
	

	
	public void runPcap(String fname) throws FileNotFoundException, IOException {
		
        final Pcap pcap = Pcap.openStream(fname);
    	System.out.println("fname=>"+ fname);
        pcap.loop(new PacketHandler() {
            @Override
            public boolean nextPacket(Packet packet) throws IOException {
            	
            	System.out.print("Protocol=>"+ packet.getProtocol());
                if (packet.hasProtocol(Protocol.TCP)) {

                    TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);
                    Buffer buffer = tcpPacket.getPayload();
                    if (buffer != null) {
                        System.out.println("TCP: " + buffer);
                    }
                } else if (packet.hasProtocol(Protocol.UDP)) {

                    UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                    Buffer buffer = udpPacket.getPayload();
                    if (buffer != null) {
                        System.out.println("UDP: " + buffer);
                    }
                }else if (packet.hasProtocol(Protocol.PCAP)) {
                	
                	 System.out.println("PCAP: "+ packet.getPacket(Protocol.PCAP));
                	 Buffer buffer=packet.getPacket(Protocol.PCAP).getPayload();
                	// packet.
                    if (buffer != null) {
                    	System.out.println("PCAP: " + buffer);
                    }
                   
                }
                return true;
            }
        });
	}
	


	
	@PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "src" ,required = true) String src) {
		try {
			runPcap(src+file.getOriginalFilename());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return src+file.getOriginalFilename();
    }

}
