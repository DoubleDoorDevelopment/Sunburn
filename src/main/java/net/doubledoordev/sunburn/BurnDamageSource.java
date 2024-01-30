package net.doubledoordev.sunburn;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BurnDamageSource extends DamageSource {
    String message;

    public BurnDamageSource(String pMessageId) {
        super(pMessageId);
    }

    @Override
    public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity pLivingEntity) {
        String[] substrings = message.split("%1\\$s", -1);
        TextComponent textComponent = new TextComponent("");

        for (String string : substrings) {
            if (string.isEmpty())
                textComponent.append(pLivingEntity.getDisplayName());
            textComponent.append(string);
        }
        return textComponent;
    }

    public BurnDamageSource customMessage(String message) {
        this.message = message;
        return this;
    }
}
