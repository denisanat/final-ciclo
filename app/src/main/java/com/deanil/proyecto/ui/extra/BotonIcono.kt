package com.deanil.proyecto.ui.extra

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.deanil.proyecto.R

class BotonIcono @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val icon: ImageView
    private val text: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.boton_icono, this, true)
        icon = findViewById(R.id.btnIconoImage)
        text = findViewById(R.id.btnIconoText)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomButton, 0, 0)
            val buttonIcon = typedArray.getResourceId(R.styleable.CustomButton_buttonIcon, 0)
            val buttonText = typedArray.getString(R.styleable.CustomButton_buttonText)
            typedArray.recycle()

            if (buttonIcon != 0) {
                icon.setImageResource(buttonIcon)
            }

            text.text = buttonText ?: ""
        }
    }

    fun setButtonIcon(resourceId: Int) {
        icon.setImageResource(resourceId)
    }

    fun setButtonIcon(drawable: Drawable) {
        icon.setImageDrawable(drawable)
    }

    fun setButtonText(text: String) {
        this.text.text = text
    }
}