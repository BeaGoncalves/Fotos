package br.com.estudos.fotosbootcamp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import br.com.estudos.fotosbootcamp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var image_uri : Uri? = null

    companion object {
        private val PERMISSION_CODE_IMAGE_PICK = 1000
        private val IMAGE_PICK_CODE = 1001
        private val PERMISSION_CODE_CAMERA_CAPTURE = 2000
        private val OPEN_CAMERA_CODE = 2001
    }

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPickMain.setOnClickListener {
            //Verifica se o sdk do dispositivo que vai executar a aplicação,
            // é maior ou igual a versão marshmallow.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // se verdadeiro, solicita as permissões
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //se o usuario nao permitiu, solicitar a permissao
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_IMAGE_PICK)
                } else {
                    // e se o usuario permitiu, chama a funcao
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }
        }

        binding.buttonOpenCameraMain.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_CAMERA_CAPTURE)
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }
        }

    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem capturada pela camera")

        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_CODE_IMAGE_PICK -> {
                //criando o alerta para o usuario permitir o acesso a fotos
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickImageFromGallery()
                } else {
                    Toast.makeText(
                        this,
                        "Permissão negada para o acesso em fotos",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            PERMISSION_CODE_CAMERA_CAPTURE -> {
                if (grantResults.size > 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Sem permissao para acesso a fotos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    //funcao responsavel por acessar a galeria de fotos
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"  // vai selecionar todos os arquvios que forem do tipo imagem
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            binding.imageViewMain.setImageURI(data?.data)
        }
        if(resultCode == Activity.RESULT_OK && requestCode == OPEN_CAMERA_CODE){
            binding.imageViewMain.setImageURI(image_uri)
        }
    }


}
