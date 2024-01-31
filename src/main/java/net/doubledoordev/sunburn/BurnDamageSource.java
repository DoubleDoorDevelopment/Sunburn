package net.doubledoordev.sunburn;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.ParametersAreNonnullByDefault;

public class BurnDamageSource extends DamageSource {
    String message;

    public BurnDamageSource(String pMessageId) {
        super(pMessageId);
    }

    @Override
    @ParametersAreNonnullByDefault
    public StringTextComponent getLocalizedDeathMessage(LivingEntity pLivingEntity) {
        String[] substrings = message.split("%1\\$s", -1);
        StringTextComponent textComponent = new StringTextComponent("");

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
