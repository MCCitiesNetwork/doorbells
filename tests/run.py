from pathlib import Path
import subprocess, zipfile, sys, os
root=Path(__file__).resolve().parents[1]; out=Path(sys.argv[2]).resolve();out.mkdir(parents=True, exist_ok=False)
server=Path(sys.argv[1]).resolve(); libs=server/'libraries'
patterns=['org/ow2/asm/*/9.9.1/*.jar','io/papermc/paper/paper-api/26.3.build.206-stable/*.jar','net/kyori/*/5.2.0/*.jar','net/kyori/option/1.1.0/*.jar','com/google/guava/guava/33.6.0-jre/*.jar','org/jetbrains/annotations/26.0.2/*.jar','com/google/code/gson/gson/2.13.2/*.jar','org/jspecify/jspecify/1.0.0/*.jar','commons-lang/commons-lang/*/*.jar','net/md-5/bungeecord-chat/*/*.jar','com/google/code/findbugs/jsr305/*/*.jar']
cp=':'.join(str(p) for pat in patterns for p in sorted(libs.glob(pat)))
java=Path(os.environ['JAVA_HOME'])/'bin'
subprocess.run([str(java/'javac'),'-cp',cp,'-d',str(out),*map(str,root.glob('*.java')),*map(str,(root/'tests').glob('*.java'))],check=True)
(out/'META-INF/services').mkdir(parents=True,exist_ok=True);(out/'META-INF/services/io.papermc.paper.registry.RegistryAccess').write_text('ProbeRegistry\n')
if len(sys.argv) > 3:
    baseline = subprocess.run([str(java/'java'), '-cp', cp+':'+str(Path(sys.argv[3]).resolve())+':'+str(out), 'Probe'], capture_output=True, text=True)
    (out/'baseline.log').write_text(baseline.stdout+baseline.stderr)
    assert baseline.returncode != 0 and 'not an enum' in baseline.stderr and 'xsound.parseSound' in baseline.stderr, baseline.stdout+baseline.stderr
    print('BASELINE: reproduced Sound interface not an enum in installed original')
subprocess.run([str(java/'java'),'--add-opens=java.base/java.lang=ALL-UNNAMED','-cp',cp+':'+str(out),'Probe'],check=True)
with zipfile.ZipFile(out/'Doorbells.jar','w',compression=zipfile.ZIP_DEFLATED) as z:
 for p in sorted((out/'me').rglob('*.class')):
  info=zipfile.ZipInfo(str(p.relative_to(out)), (2026,11,1,0,0,0)); info.compress_type=zipfile.ZIP_DEFLATED; z.writestr(info,p.read_bytes())
 for name in ['plugin.yml','config.yml']:
  info=zipfile.ZipInfo(name,(2026,11,1,0,0,0)); info.compress_type=zipfile.ZIP_DEFLATED; z.writestr(info,(root/name).read_bytes())
