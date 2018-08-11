package main.java.com.raphydaphy.betterbeacons.core;

import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.mixin.Mixins;

public class BetterBeaconsCore implements InitializationListener
{
    @Override
    public void onInitialization()
    {
        Mixins.addConfiguration("mixins.betterbeacons.json");
    }
}
