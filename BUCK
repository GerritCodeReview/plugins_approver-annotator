include_defs('//bucklets/gerrit_plugin.bucklet')

gerrit_plugin(
  name = 'approver-annotator',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Implementation-Title: Approver Annotator plugin',
    'Implementation-URL: https://gerrit-review.googlesource.com/#/admin/projects/plugins/approver-annotator',
    'Gerrit-PluginName: approver-annotator',
    'Gerrit-Module: com.googlesource.gerrit.plugins.annotator.ApproverAnnotator$Module',
    'Gerrit-ApiType: plugin',
    'Gerrit-ApiVersion: 2.13-SNAPSHOT',
  ],
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
  name = 'classpath',
  deps = [':approver-annotator__plugin'],
)
