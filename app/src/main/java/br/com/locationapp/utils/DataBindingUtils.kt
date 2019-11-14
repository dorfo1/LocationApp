package br.com.locationapp.utils

import android.widget.Button
import androidx.databinding.BindingAdapter

@BindingAdapter("app:localizando")
fun Button.Localizando(boolean: Boolean){
    text = if(boolean) "Parar Localização" else "Começar Localização"
}