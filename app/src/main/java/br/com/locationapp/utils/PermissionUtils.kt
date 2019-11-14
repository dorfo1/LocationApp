package br.com.locationapp.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object PermissionUtils{

    fun validarPermissoes(permissoes: List<String>, activity: FragmentActivity?, requestCode:Int) : Boolean{

        val listaPermissoes = ArrayList<String>()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permissao in permissoes){
                val temPermissao = ContextCompat.checkSelfPermission(activity as Activity,permissao) == PackageManager.PERMISSION_GRANTED
                if(!temPermissao) listaPermissoes.add(permissao)
            }

            if(listaPermissoes.isEmpty()) return true
            else{
                ActivityCompat.requestPermissions(activity as Activity,listaPermissoes.toTypedArray(),requestCode)
            }
        }
        return true
    }
}