package mbbank.secure.obfuscator

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.ReadOnlyProductFlavor
import mbbank.secure.obfuscator.core.ObfDex
import org.gradle.api.*

class ObfPlugin implements Plugin<Project> {
    private String PLUGIN_NAME = "MbbankObfuscator"
    private Project mProject
    public static MbbankObfuscatorExtension sObfuscatorExtension
    public Map<String, String> mTaskMapping = new HashMap<>()

    void apply(Project project) {
        this.mProject = project
        def android = project.extensions.findByType(AppExtension)
        project.configurations.create(PLUGIN_NAME).extendsFrom(project.configurations.implementation)
        sObfuscatorExtension = project.extensions.create(PLUGIN_NAME, MbbankObfuscatorExtension, project)

        mTaskMapping.clear()
        project.afterEvaluate {
            System.out.println(sObfuscatorExtension.toString())
        }

        project.afterEvaluate { ->
            if (!sObfuscatorExtension.enabled) {
                return
            }
            def action = new Action<Task>() {
                @Override
                void execute(Task task) {
                    task.getOutputs().getFiles().collect().each() { element ->
                        def file = new File(element.toString())
                        ObfDex.obf(file.getAbsolutePath(),
                                sObfuscatorExtension.depth,
                                sObfuscatorExtension.obfClass,
                                sObfuscatorExtension.blackClass,
                                mTaskMapping.get(task.name))
                    }
                }
            }
            List<Task> tasks = new ArrayList<>()
            if (android != null) {
                android.applicationVariants.all(new Action<ApplicationVariant>() {
                    @Override
                    void execute(ApplicationVariant applicationVariant) {
                        File mappingFile = null
                        if (applicationVariant.buildType.minifyEnabled) {
                            mappingFile = applicationVariant.mappingFile
                        }
                        def buildType = upperCaseFirst(applicationVariant.buildType.name)
                        boolean empty = true
                        for (ReadOnlyProductFlavor flavor : applicationVariant.productFlavors) {
                            def flavorName = upperCaseFirst(applicationVariant.flavorName)
                            addOtherTask(tasks, flavorName, buildType, mappingFile)
                            empty = false
                            break
                        }
                        if (empty) {
                            addOtherTask(tasks, "", buildType, mappingFile)
                        }
                    }
                })
            }

            for (Task task : tasks) {
                task.doLast(action)
            }
            if (tasks.isEmpty()) {
                System.err.println("This gradle version is not applicable")
            }
        }
    }

    private void addOtherTask(List<Task> tasks, String name, String buildType, File mappingFile) {
        addTask("mergeDex${name}${buildType}", tasks, mappingFile)
        addTask("mergeLibDex${name}${buildType}", tasks, mappingFile)
        addTask("mergeProjectDex${name}${buildType}", tasks, mappingFile)
        addTask("transformDexArchiveWithDexMergerFor${name}${buildType}", tasks, mappingFile)
        addTask("minify${name}${buildType}WithR8", tasks, mappingFile)

        println("$name$buildType mappingFile $mappingFile")
    }

    private static String upperCaseFirst(String val) {
        char[] arr = val.toCharArray()
        arr[0] = Character.toUpperCase(arr[0])
        return new String(arr)
    }

    private void addTask(String name, List<Task> tasks, File mappingFile) {
        try {
            //Protected code
            Task task = mProject.tasks.getByName(name)
            if (!tasks.contains(task)) {
                tasks.add(task)
                if (mappingFile != null) {
                    mTaskMapping.put(task.name, mappingFile.absolutePath)
                }
                println("add Task $name")
            }
        } catch(UnknownTaskException ignored) {
            //Catch block
        }
    }
}