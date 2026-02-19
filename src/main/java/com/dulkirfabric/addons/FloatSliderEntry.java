package com.dulkirfabric.addons;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import kotlinx.atomicfu.AtomicRef;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus.Internal;

@Environment(EnvType.CLIENT)
public class FloatSliderEntry extends TooltipListEntry<Float> {
    protected Slider sliderWidget;
    protected Button resetButton;
    protected AtomicReference<Float> value;
    protected final float original;
    private float minimum;
    private float maximum;
    private final Supplier<Float> defaultValue;
    private Function<Float, Component> textGetter;
    private final List<AbstractWidget> widgets;

    /** @deprecated */
    @Deprecated
    @Internal
    public FloatSliderEntry(Component fieldName, float minimum, float maximum, float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer) {
        this(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, (Supplier)null);
    }

    /** @deprecated */
    @Deprecated
    @Internal
    public FloatSliderEntry(Component fieldName, float minimum, float maximum, float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier) {
        this(fieldName, minimum, maximum, value, resetButtonKey, defaultValue, saveConsumer, tooltipSupplier, false);
    }

    /** @deprecated */
    @Deprecated
    @Internal
    public FloatSliderEntry(Component fieldName, float minimum, float maximum, float value, Component resetButtonKey, Supplier<Float> defaultValue, Consumer<Float> saveConsumer, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);
        this.textGetter = (integer) -> Component.literal(String.format("Value: %.2f", integer));
        this.original = value;
        this.defaultValue = defaultValue;
        this.value = new AtomicReference<Float>(value);
        this.saveCallback = saveConsumer;
        this.maximum = maximum;
        this.minimum = minimum;
        this.sliderWidget = new Slider(0, 0, 152, 20, ((double)this.value.get() - (double)minimum) / (double)Math.abs(maximum - minimum));
        this.resetButton = Button.builder(resetButtonKey, (widget) -> this.setValue((int) defaultValue.get().floatValue())).bounds(0, 0, Minecraft.getInstance().font.width(resetButtonKey) + 6, 20).build();
        this.sliderWidget.setMessage((Component)this.textGetter.apply(this.value.get()));
        this.widgets = Lists.newArrayList(new AbstractWidget[]{this.sliderWidget, this.resetButton});
    }

    public Function<Float, Component> getTextGetter() {
        return this.textGetter;
    }

    public FloatSliderEntry setTextGetter(Function<Float, Component> textGetter) {
        this.textGetter = textGetter;
        this.sliderWidget.setMessage((Component)textGetter.apply(this.value.get()));
        return this;
    }

    public Float getValue() {
        return this.value.get();
    }

    /** @deprecated */
    @Deprecated
    public void setValue(int value) {
        this.sliderWidget.setValue((double)(Mth.clamp(value, this.minimum, this.maximum) - this.minimum) / (double)Math.abs(this.maximum - this.minimum));
        this.value.set(Math.min(Math.max(value, this.minimum), this.maximum));
        this.sliderWidget.updateMessage();
    }

    public boolean isEdited() {
        return super.isEdited() || this.getValue().floatValue() != this.original;
    }

    public Optional<Float> getDefaultValue() {
        return this.defaultValue == null ? Optional.empty() : Optional.ofNullable((Float)this.defaultValue.get());
    }

    public List<? extends GuiEventListener> children() {
        return this.widgets;
    }

    public List<? extends NarratableEntry> narratables() {
        return this.widgets;
    }

    public FloatSliderEntry setMaximum(int maximum) {
        this.maximum = maximum;
        return this;
    }

    public FloatSliderEntry setMinimum(int minimum) {
        this.minimum = minimum;
        return this;
    }

    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();
        this.resetButton.active = this.isEditable() && this.getDefaultValue().isPresent() && (Float)this.defaultValue.get() != this.value.get();
        this.resetButton.setY(y);
        this.sliderWidget.active = this.isEditable();
        this.sliderWidget.setY(y);
        Component displayedFieldName = this.getDisplayedFieldName();
        if (Minecraft.getInstance().font.isBidirectional()) {
            graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x);
            this.sliderWidget.setX(x + this.resetButton.getWidth() + 1);
        } else {
            graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
            this.resetButton.setX(x + entryWidth - this.resetButton.getWidth());
            this.sliderWidget.setX(x + entryWidth - 150);
        }

        this.sliderWidget.setWidth(150 - this.resetButton.getWidth() - 2);
        this.resetButton.render(graphics, mouseX, mouseY, delta);
        this.sliderWidget.render(graphics, mouseX, mouseY, delta);
    }

    private class Slider extends AbstractSliderButton {
        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1) {
            super(int_1, int_2, int_3, int_4, Component.empty(), double_1);
        }

        public void updateMessage() {
            this.setMessage((Component)FloatSliderEntry.this.textGetter.apply(FloatSliderEntry.this.value.get()));
        }

        protected void applyValue() {
            FloatSliderEntry.this.value.set((float) (FloatSliderEntry.this.minimum + Math.abs(FloatSliderEntry.this.maximum - FloatSliderEntry.this.minimum) * this.value));
        }

        public boolean keyPressed(KeyEvent event) {
            return FloatSliderEntry.this.isEditable() && super.keyPressed(event);
        }

        public boolean mouseDragged(MouseButtonEvent event, double double_3, double double_4) {
            return FloatSliderEntry.this.isEditable() && super.mouseDragged(event, double_3, double_4);
        }

        public double getProgress() {
            return this.value;
        }

        public void setProgress(double integer) {
            this.value = integer;
        }

        public void setValue(double integer) {
            this.value = integer;
        }
    }
}
