package cn.wzbos.android.chihiro.utils


class ExecUtils {
    static int exec(String... cmd) {
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd)
            builder.redirectErrorStream(true)
            Process process = builder.start()
            InputStream stdout = process.getInputStream()

            BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))
            String line
            while ((line = reader.readLine()) != null) {
                Logger.w(line)
            }
            process.waitFor()
            int extCode = process.exitValue()
            Logger.w("exit: " + extCode)
            return extCode
        } catch (Exception e) {
            Logger.e("exec failed!", e.getMessage())
            e.printStackTrace()
            return -1
        }
    }
}
