package com.ofekn.quick.neoforge.client;

import com.ofekn.quick.impl.client.QuickClient;
import com.ofekn.quick.impl.common.Quick;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Quick.MID, dist = Dist.CLIENT)
public class QuickNeoforgeClient {
    public QuickNeoforgeClient() {
        QuickClient.init();
    }
}
