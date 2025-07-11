package com.curesky.netserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.curesky.netserver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val frpsBinName = "frps"      // 可执行文件名
    private val frpsConfigName = "frps.ini" // 配置文件

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化FRPS文件
        initFrpsFiles()

        // 直接执行 FRPS（不再依赖按钮点击）
        runFrps()

        // 可选：列出文件（调试用）
        listPrivateFiles()
    }

    /**
     * 将assets中的frps文件复制到私有目录
     */
    private fun initFrpsFiles() {
        try {
            // 1. 创建bin目录（/data/data/包名/files/bin）
            val binDir = File(filesDir, "bin").apply {
                if (!exists()) mkdirs()
            }
            // 2. 复制可执行文件
            copyAssetToFile(frpsBinName, File(binDir, frpsBinName).apply {
                // 添加执行权限（仅Linux有效）
                setExecutable(true)
            })
            // 3. 复制配置文件
            copyAssetToFile(frpsConfigName, File(filesDir, frpsConfigName))
        } catch (e: Exception) {
            binding.textView.text = "初始化FRPS失败: ${e.message}"
        }
    }

    /**
     * 从assets复制文件到目标位置
     */
    private fun copyAssetToFile(assetName: String, targetFile: File) {
        try {
            assets.open(assetName).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            throw IOException("复制 $assetName 失败", e)
        }
    }

    /**
     * 执行frps（需要处理权限问题）
     */
    private fun runFrps() {
        val frpsPath = File(filesDir, "bin/$frpsBinName").absolutePath
        val configPath = File(filesDir, frpsConfigName).absolutePath
        try {
            // 执行命令
//            binding.textView.append("\n\n🚀 [FRPS 服务]")

            Runtime.getRuntime().exec("$frpsPath -c $configPath")
        } catch (e: Exception) {
            binding.textView.append("\n\n❌ FRPS启动失败: ${e.message}")
        }
    }

    /**
     * 列出私有目录文件（调试用）
     */
    private fun listPrivateFiles() {
        val sb = StringBuilder("📁 私有目录文件列表:\n\n")

        // 列出所有目录
        listFilesInDirectory(filesDir, sb)
        // 显示结果
        binding.textView.text = sb.toString()
    }

    private fun listFilesInDirectory(dir: File, sb: StringBuilder, indent: String = "") {
        dir.listFiles()?.forEach { file ->
            val prefix = if (file.isDirectory) "📂 " else "📄 "
            sb.append("$indent$prefix${file.name}\n")
            if (file.isDirectory) {
                listFilesInDirectory(file, sb, "$indent    ")
            }
        }
    }
}
