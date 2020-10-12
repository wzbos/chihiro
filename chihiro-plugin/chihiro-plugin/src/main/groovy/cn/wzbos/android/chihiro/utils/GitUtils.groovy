package cn.wzbos.android.chihiro.utils


/**
 * git utils
 * Created by wuzongbo on 2020/09/17.
 */
class GitUtils {
    static int clone(String url, String branch, String directory) {
        def cmd = "git clone -b ${branch} ${url} ${directory}"
        return exec(cmd)
    }

    static int exec(String cmd) {
        Logger.w("$cmd")
        ProcessBuilder builder = new ProcessBuilder(cmd.split(" "))
        builder.redirectErrorStream(true)
        Process process = builder.start()
        InputStream stdout = process.getInputStream()
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))
        String line
        while ((line = reader.readLine()) != null) {
            Logger.w(line)
        }
        process.waitFor()
        return process.exitValue()
    }


    static String getGitBranch() {
        try {
            return 'git symbolic-ref --short -q HEAD'.execute().text.trim()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return ""
    }

    static String getGitUsername() {
        try {
            return 'git config user.name'.execute().text.trim()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return ""
    }


}
