# JSXE English properties file
# $Id:
# Maintained by Alexandr Gridnev (alexandr-gridnev@yandex.ru)
#:mode=properties:
#:tabSize=4:indentSize=4:noTabs=true:
#:folding=explicit:collapseFolds=1:

#{{{ common properties

common.ok=Принять
common.cancel=Отменить
common.open=Открыть
common.save=Сохранить
common.save.as=Сохранить как
common.close=Закрыть
common.apply=Применить
common.more=Еще
common.insert=Вставить
common.add=Добавить
common.remove=Удалить
common.moveUp=Поднять
common.moveDown=Опустить
common.cut=Вырезать
common.copy=Копировать
common.paste=Вставить
common.find=Искать...
common.findnext=Искать следующий

common.ctrl=Ctrl
common.alt=Alt
common.shift=Shift
common.meta=Meta

#}}}

#{{{ XML Terminology

#{{{ XML Node Types
xml.element=Элемент
xml.processing.instruction=Правило обработки
xml.cdata=Секция CDATA
xml.text=Текст
xml.entity.reference=Ссылка на сущность
xml.declaration=Объявление элемента
xml.notation=Замечание
xml.entity=Объявление сущности
xml.comment=Комментарий
xml.attribute=Свойство
xml.doctype=Тип документа
#}}}

xml.attribute.value.short=Значение
xml.attribute.value=Значение свойства
xml.document=документ XML
xml.namespace=Пространство имен
xml.namespace.prefix=Префикс пространства имен
xml.namespace.prefix.short=Префикс
xml.namespace.decl=Объявление пространства имен
xml.doctypedef=Определение типа документа (DTD)
xml.doctype.name=Имя
xml.doctype.public=Публичный Id
xml.doctype.system=Системный Id
xml.schema=Схема XML
#}}}

#{{{ Global Options
Global.Options.Dialog.Title=Глобальные опции
Global.Options.Title=Главное
Global.Options.Max.Recent.Files=Помнить ранее открытых файлов:
Global.Options.Max.Recent.Files.ToolTip=Максимальное количество ранее открытых файлов, про которые jsXe помнит и отображает в меню Файл->Ранее открытые файлы
Global.Options.network-off=Кешировать всегда, без спросу
Global.Options.network-cache=Кешировать удаленные файлы только спросив разрешения
Global.Options.network-always=Скачивать всегда, без спросу
Global.Options.network=Скачивание DTD и схемы:
Global.Options.Menu.Spill.Over=Меню выпадают после:

Shortcuts.Options.Title=Горячие клавиши
Shortcuts.Options.Select.Label=Редактировать горячие клавиши:
Shortcuts.Options.Shortcut=Горячая клавиша
Shortcuts.Options.Command=Команда

Grab.Key.title=Нажмите клавишу
Grab.Key.caption=Нажмите клавишу для команды "{0}", а котом нажмите Принять.
Grab.Key.keyboard-test=Input the key strokes that are causing problems.
Grab.Key.assigned-to=Уже назначено для: {0}
Grab.Key.assigned-to.none=<ничего>
Grab.Key.clear=Очистить
Grab.Key.remove=Удалить текущее

Grab.Key.remove-ask.title=Удалить клавишу?
Grab.Key.remove-ask.message=\
	Вы не назначили новую клавишу.\n\
	Вы что, всеръез решили лишить команду клавиши, правда?

Grab.Key.duplicate-alt-shortcut.title=Две команды на клавишу
Grab.Key.duplicate-alt-shortcut.message=\
	Клавиша которую вы назначили уже занята за другой командой\n\
	Просьба выбрать другую, ато они подерутся.

Grab.Key.duplicate-shortcut.title=Две команды на клавишу
Grab.Key.duplicate-shortcut.message=\
	Эта клавиша уже занята за\n\
	"{0}".\n\
	\n\
	Вы хотите отвергнуть это назначение?
#}}}

#{{{ Document Options
Document.Options.Title=Опции документа XML
Document.Options.Message=Этот диалог управляет настройками только\n текущего документа. Чтобы изменить\n базовые настройки лезте в\n Инструменты->Глобальные опции.
Document.Options.Line.Separator=Разделитель строк:
Document.Options.Line.Separator.ToolTip=Разделитель строк. По умолчанию выбирается традиционный для вашей операционной системы.
Document.Options.Encoding=Кодировка:
Document.Options.Encoding.ToolTip=Кодировка, которую jsXe использует для записи текстовой инфы в файл.
Document.Options.Indent.Width=Ширина отступа:
Document.Options.Indent.Width.ToolTip=Ширина используемая при форматировании и отображении символов табуляции.
Document.Options.Format.XML=Форматировать выводимый XML
Document.Options.Format.XML.ToolTip=Если это выбрано, XML будет форматироваться автоматически.
Document.Options.Validate=Подтв. если доступны DTD или Схема
Document.Options.Validate.ToolTip=Если это выбрано, jsXe будет подтверждать (проверять?) документ XML.
Document.Options.Soft.Tabs=Мягкие табуляции (эмуляция пробелами)
Document.Options.Soft.Tabs.ToolTip=Если это выбрано, символы табуляции будут заменены пробелами.
#}}}

#{{{ Plugin Manager
Plugin.Manager.Title=Менеджер плагинов
Plugin.Manager.Name.Column.Header=Имя
Plugin.Manager.Version.Column.Header=Версия
Plugin.Manager.Status.Column.Header=Статус
Plugin.Manager.Loaded.Status=Загружен
Plugin.Manager.Not.Loaded.Status=Не загружен
Plugin.Manager.Broken.Status=Ошибка
#}}}

#{{{ Menu Items

File.Menu=Файл
Edit.Menu=Правка
View.Menu=Вид
Tools.Menu=Инструменты
Help.Menu=Справка

#{{{ File Menu Items
new-file.label=Новый
open-file.label=Открыть...
File.Recent=Ранее открытые файлы
File.Recent.None=Нэма нифигга
save-file.label=Сохранить
save-as.label=Сохранить как...
reload-file.label=Открыть еще раз
close-file.label=Закрыть
close-all.label=Закрыть все
exit.label=Выход
#}}}

general-options.label=Глобальные опции...
document-options.label=Опции документа...
plugin-manager.label=Менеджер плагинов...
validation-errors.label=Ошибки подтверждения...
about-jsxe.label=О программе jsXe...
#}}}

#{{{ Messages

DocumentView.Open.Message="Не могу открыть буффер в каком-либо из установленных document view-ов"

#{0} file name
DocumentBuffer.Reload.Message={0} не сохранен!\n Вы потеряете все несохраненные изменения если продолжите!\n\n Продолжить? Вам не нужны эти несохраненные данные, в самом деле?
DocumentBuffer.Reload.Message.Title=Документ был изменен
DocumentBuffer.SaveAs.Message=Файл {0} уже существует. Записать новый файл поверх, зверски убив старый?
DocumentBuffer.SaveAs.Message.Title=Файл уже существует
DocumentBuffer.Close.Message={0} Не сохранен! Сохранить его прямо сейчас?
DocumentBuffer.Close.Message.Title=Несохраненные изменения

#{0} file name
DocumentBuffer.Saved.Message={0} Сохранен
DocumentBuffer.Closed.Message={0} Закрыт
DocumentBuffer.Loaded.Message={0} Загружен
DocumentBuffer.Reloaded.Message={0} Загружен заново

#{0} plugin name
DocumentView.Not.Found=Нету такого плагина со странным именем "{0}".

#{{{ Plugin Load Messages

# {0} plugin name
Plugin.Error.title=Ошибка плагина
Plugin.Error.List.message=Следующие плагины не могут быть загружены:
Plugin.Load.Already.Loaded=Плагин {0} уже загружен.
Plugin.Load.Wrong.Main.Class=Главный класс не является классом плагина.
Plugin.Load.No.Plugin.Class=Класс плагина не был определен.
Plugin.Load.No.Plugin.Name=Имя плагина не было определено.
Plugin.Load.No.Plugin.HR.Name=Для плагина не было определено такого имени,/n чтоб его мог прочитать нормальный человек (human-readable name).
Plugin.Load.No.Plugin.Version=Версия плагина не была определена.

# {0} plugin name
# {1} requirement name
# {2} required version
# {3} found version
Plugin.Dependency.Message={0} требует {1} версии {2}, но обнаружена только версия {3}.
# {0} plugin name
# {1} requirement name
# {2} required version
Plugin.Dependency.Not.Found={0} требует {1} версии {2}, но {1} не удалось найти.
# {0} plugin name
# {1} requirement name
Plugin.Dependency.Not.Found2={0} требует {1} но {1} нигде не видать.
# {0} plugin name
# {1} invalid dependency
Plugin.Dependency.Invalid=У {0} есть неправильная зависимость: {1}
# {0} invalid version
Plugin.Dependency.Invalid.jsXe.Version=Неправильный номер версии jsXe: {0}
# {0} plugin name
# {1} requirement name
Plugin.Dependency.No.Version=Не могу загрузить плагин {0}, потому что у {1} не подписана версия.
# {0} plugin name
# {1} requirement name
Plugin.Dependency.Failed.Load={0} Требует плагин {1}, но этот самый {1} так и не удалось загрузить.

#}}}

#{{{ Error Messages
No.Xerces.Error.message={0} не обнаружен. jsXe требует Apache {0}.
No.Xerces.Error.title={0} не обнаружен.
IO.Error.title=ошибка ввода/вывода (I/O Error)
IO.Error.message=Произошла ошибка ввода/вывода
#}}}

#}}}

#{{{ Dialogs

#{{{ Download resource dialog
xml.download-resource.title=Выкачиваем ресурсы с удаленного сервера...

#{0} URL
xml.download-resource.message=Этот XML-файл зависит от ресурсов, которые расколожены\n\
	по следующему адресу в интернете:\n\
	\n\
	{0}\n\
	\n\
	Хотите ли вы чтобы плагин XML скачал ресурсы и закешировал их\n\
	для последующего использования?\n\
	\n\
	Если вы хотите соизволить разрешить мне скачивать ресурсы без спросу,\n\
	то отключите кеширование ресурсов в глобальных опциях jsXe.
#}}}

#{{{ Dirty Files Dialog
DirtyFilesDialog.Dialog.Title=Несохраненные изменения
DirtyFilesDialog.Dialog.Message=В следующих файлах есть несохраненные изменения:
DirtyFilesDialog.Button.SelectAll.Title=Выбрать все
#DirtyFilesDialog.Button.SelectAll.ToolTip=Взять их фсехх!
DirtyFilesDialog.Button.SaveSelected.Title=Сохранить выбранные
#DirtyFilesDialog.Button.SaveSelected.ToolTip=Сохраняет, что тут еще можно сказать... те что выбраны, да...
DirtyFilesDialog.Button.DiscardSelected.Title=Убрать выделение
#DirtyFilesDialog.Button.DiscardSelected.ToolTip=Убирает выделение ;)
#}}}

#{{{ Activity Log Dialog
activity-log.label = Лог активности
ActivityLogDialog.Dialog.Title = Лог активности
ActivityLogDialog.Dialog.Message = Содержимое лога активности:
#}}}

#{{{ About dialag
about.title=О программе jsXe
about.message=Распространяется на условиях GNU General Public License\n\n\
    Активные разработчики:\n\
    \ \ \ \ Ian Lewis <IanLewis@member.fsf.org>\n\
    \ \ \ \ Trish Hartnett <trishah136@users.sourceforge.net>\n\n\
    Переводчики:\n\
    \ \ \ \ German (de) - Bianca Sh\u00f6en\n\
    \ \ \ \ Swedish (sv) - Patrik Johansson <patjoh@itstud.chalmers.se>\n\n\
    В прошлом учавствовали в разработке:\n\
    \ \ \ \ Aaron Flatten  <aflatten@users.sourceforge.net>\n\
    \ \ \ \ Bilel Remmache <rbilel@users.sourceforge.net>\n\
    \ \ \ \ SVM <svmcoranto@users.sourceforge.net>\n\n\
    Страница проекта: http://jsxe.sourceforge.net/
#}}}

#{{{ Validation Errors Dialog
ValidationErrors.title=Ошибки подтверждения
ValidationErrors.message=Ошибки подтверждения
#}}}

#}}}
