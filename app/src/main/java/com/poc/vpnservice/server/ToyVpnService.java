package com.poc.vpnservice.server;


import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import com.poc.vpnservice.activity.MainActivity;
import com.poc.vpnservice.fragment.LandingPageStatusTabFragment;
import com.poc.vpnservice.util.SLog;


public class ToyVpnService extends VpnService
{


    private static final String TAG = "ToyVpnService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SLog.e(TAG, "Start service");
        run();
        return super.onStartCommand(intent, flags, startId);
    }

    private void run() {
        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                initPort();
            }
        });
    }

    private void initPort() {
        SLog.e(TAG, "Outgoing port");

        Builder builder = new Builder();
        //That is, the maximum transmission unit of the virtual network port,
        // if the length of the transmitted packet exceeds this number,
        // it will be subcontracted;

        builder.setMtu(1500);

        //Address, the IP address of this virtual network port;
        builder.addAddress("199.58.84.167", 24);

//        In fact, this is not used to modify the routing table on the Android device.
//          Instead, it is used to change the NAT table of iptables, only matching the IP packets,
//         Will be forwarded to the virtual port. If it is 0.0.0.0/0,
//         Will forward all IP packets to the virtual port via NAT.；

        builder.addRoute("0.0.0.0", 0);
        //The DNS server address of the port;
        // builder.addDnsServer(...);

        // Is to add the DNS domain name automatically. The DNS server must search through the
        // full domain name. However, it is too much trouble to enter the full domain name for each search.
        // It can be simplified by configuring the automatic completion rules of the domain name.
        // builder.addSearchDomain(...);

        //Is the name of the VPN connection you want to establish,
        // It will be displayed in the system-managed notification bar and dialog box associated with
        // the VPN connection;
        builder.setSession("guomin");

        //This intent points to a configuration page that is used to configure the VPN link.
        // It is not necessary,
        //  If it is not set, the configuration button will not
        // appear in the VPN related dialog box that pops up.

        // builder.setConfigureIntent(...);

        //If everything is ok, the tun0 virtual network interface is set up.
        // Also, the iptables command will be used to modify the NAT table
        // and forward all data to the tun0 interface.
        ParcelFileDescriptor mInterface = builder.establish();

        FileInputStream in = new FileInputStream(
                mInterface.getFileDescriptor());

        //b. Packets received need to be written to this output stream.
        FileOutputStream out = new FileOutputStream(
                mInterface.getFileDescriptor());
        try {

            //c. The UDP channel can be used to pass/get ip package to/from server
            DatagramChannel tunnel = DatagramChannel.open();

            // Connect to the server, localhost is used for demonstration only.
            tunnel.connect(new InetSocketAddress("127.0.0.1", 8087));
            //d. Protect this socket, so package send by it will not be feedback to the vpn service.
            protect(tunnel.socket());

            //e. Use a loop to pass packets.
            while (true) {
                //get packet with in
                //put packet to tunnel
                //get packet form tunnel
                //return packet with out
                //sleep is a must
                Thread.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            SLog.e(TAG, "Close Port");
            closeInterface(mInterface);
        }

    }

    private void closeInterface(ParcelFileDescriptor mInterface) {
        if (mInterface == null) {
            return;
        }
        try {
            mInterface.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mInterface = null;
            SLog.e(TAG, "Close Interface");
        }
    }

    public static void startService(MainActivity context) {
        Intent intent = new Intent(context, ToyVpnService.class);
        context.startService(intent);
    }
}
