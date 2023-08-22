package mbbank.secure.obfuscator
import org.gradle.api.Project

class MbbankObfuscatorExtension {
    boolean enabled = false
    int depth = 1
    String[] obfClass = []
    String[] blackClass = []

    MbbankObfuscatorExtension(Project project) {

    }


    @Override
    public String toString() {
        return "MbbankObfuscatorExtension{" +
                "enabled=" + enabled +
                ", depth=" + depth +
                ", obfClass=" + Arrays.toString(obfClass) +
                ", blackClass=" + Arrays.toString(blackClass) +
                '}';
    }
}