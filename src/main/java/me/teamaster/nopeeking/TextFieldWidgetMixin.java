package me.teamaster.nopeeking;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.objectweb.asm.Opcodes;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin {
    @Redirect(method = "renderButton", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;text:Ljava/lang/String;"))
    private String passwordObfuscationProxy(TextFieldWidget textFieldWidget) {
        String text = textFieldWidget.getText();
        if (MinecraftClient.getInstance().currentScreen instanceof ChatScreen) {
            String[] splitText = text.split(" ", -1);
            if (splitText.length > 1 && (splitText[0].equals("/l") || splitText[0].equals("/login") || splitText[0].equals("/register"))) {
                StringBuilder obfuscatedText = new StringBuilder();
                boolean firstPart = true;
                for (int i = 0; i < splitText.length; i++) {
                    String part = splitText[i];
                    if (firstPart) {
                        obfuscatedText.append(part);
                        firstPart = false;
                        obfuscatedText.append(' ');
                    } else {
                        if (!part.isEmpty()) {
                            obfuscatedText.append("*".repeat(part.length()));
                        }
                        if (i < splitText.length - 1) {
                            obfuscatedText.append('*');
                        }
                    }
                }
                return obfuscatedText.toString();
            }
        }
        return text;
    }
}
