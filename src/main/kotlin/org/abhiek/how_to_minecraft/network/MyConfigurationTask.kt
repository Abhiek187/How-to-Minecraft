package org.abhiek.how_to_minecraft.network

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.network.ConfigurationTask
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask
import java.util.function.Consumer

class MyConfigurationTask(val listener: ServerConfigurationPacketListener): ICustomConfigurationTask {
    override fun run(sender: Consumer<CustomPacketPayload>) {
        val payload = MyData(name = "Steve", age = 16)
        sender.accept(payload)
        this.listener.finishCurrentTask(this.type())
    }

    override fun type(): ConfigurationTask.Type {
        return TYPE
    }

    companion object {
        val TYPE: ConfigurationTask.Type =
            ConfigurationTask.Type(ResourceLocation.fromNamespaceAndPath("mymod", "my_task"))
    }
}