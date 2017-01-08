package org.joyfulframework.notification.mail;

import org.apache.commons.lang3.StringUtils;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by richey on 17-1-8.
 */
public class MimeUtil {
    public MimeUtil() {
    }

    public static final List<InternetAddress> parseAddress(String address, String encoding) {
        if(StringUtils.isEmpty(address)) {
            return Collections.emptyList();
        } else {
            try {
                InternetAddress[] ex = InternetAddress.parse(address);
                List returned = new ArrayList();
                InternetAddress[] var4 = ex;
                int var5 = ex.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    InternetAddress raw = var4[var6];
                    returned.add(encoding != null?new InternetAddress(raw.getAddress(), raw.getPersonal(), encoding):raw);
                }

                return returned;
            } catch (Exception var8) {
                throw new RuntimeException("Failed to parse embedded personal name to correct encoding", var8);
            }
        }
    }
}
